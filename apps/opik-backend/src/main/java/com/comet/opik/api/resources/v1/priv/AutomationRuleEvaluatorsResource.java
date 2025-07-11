package com.comet.opik.api.resources.v1.priv;

import com.codahale.metrics.annotation.Timed;
import com.comet.opik.api.BatchDelete;
import com.comet.opik.api.LogCriteria;
import com.comet.opik.api.Page;
import com.comet.opik.api.evaluators.AutomationRuleEvaluator;
import com.comet.opik.api.evaluators.AutomationRuleEvaluatorUpdate;
import com.comet.opik.domain.evaluators.AutomationRuleEvaluatorService;
import com.comet.opik.infrastructure.auth.RequestContext;
import com.comet.opik.infrastructure.ratelimit.RateLimited;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.UUID;

import static com.comet.opik.api.LogItem.LogPage;
import static com.comet.opik.api.evaluators.AutomationRuleEvaluator.AutomationRuleEvaluatorPage;
import static com.comet.opik.api.evaluators.AutomationRuleEvaluator.View;

@Path("/v1/private/automations/evaluators/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Tag(name = "Automation rule evaluators", description = "Automation rule evaluators resource")
public class AutomationRuleEvaluatorsResource {

    private final @NonNull AutomationRuleEvaluatorService service;
    private final @NonNull Provider<RequestContext> requestContext;

    @GET
    @Operation(operationId = "findEvaluators", summary = "Find project Evaluators", description = "Find project Evaluators", responses = {
            @ApiResponse(responseCode = "200", description = "Evaluators resource", content = @Content(schema = @Schema(implementation = AutomationRuleEvaluatorPage.class)))
    })
    @JsonView(View.Public.class)
    public Response find(@QueryParam("project_id") UUID projectId,
            @QueryParam("name") String name,
            @QueryParam("page") @Min(1) @DefaultValue("1") int page,
            @QueryParam("size") @Min(1) @DefaultValue("10") int size) {

        String workspaceId = requestContext.get().getWorkspaceId();
        log.info("Looking for automated evaluators for project id '{}' on workspaceId '{}' (page {})", projectId,
                workspaceId, page);
        Page<AutomationRuleEvaluator<?>> evaluatorPage = service.find(projectId, workspaceId, name, page, size);
        log.info("Found {} automated evaluators for project id '{}' on workspaceId '{}' (page {}, total {})",
                evaluatorPage.size(), projectId, workspaceId, page, evaluatorPage.total());

        return Response.ok()
                .entity(evaluatorPage)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "getEvaluatorById", summary = "Get automation rule evaluator by id", description = "Get automation rule by id", responses = {
            @ApiResponse(responseCode = "200", description = "Automation Rule resource", content = @Content(schema = @Schema(implementation = AutomationRuleEvaluator.class)))
    })
    @JsonView(View.Public.class)
    public Response getEvaluator(@QueryParam("project_id") UUID projectId, @PathParam("id") UUID evaluatorId) {
        String workspaceId = requestContext.get().getWorkspaceId();

        log.info("Looking for automated evaluator: id '{}' on project_id '{}'", projectId, workspaceId);
        AutomationRuleEvaluator<?> evaluator = service.findById(evaluatorId, projectId, workspaceId);
        log.info("Found automated evaluator: id '{}' on project_id '{}'", projectId, workspaceId);

        return Response.ok().entity(evaluator).build();
    }

    @POST
    @Operation(operationId = "createAutomationRuleEvaluator", summary = "Create automation rule evaluator", description = "Create automation rule evaluator", responses = {
            @ApiResponse(responseCode = "201", description = "Created", headers = {
                    @Header(name = "Location", required = true, example = "${basePath}/v1/private/automations/projects/{projectId}/evaluators/{evaluatorId}", schema = @Schema(implementation = String.class))
            })
    })
    @RateLimited
    public Response createEvaluator(
            @RequestBody(content = @Content(schema = @Schema(implementation = AutomationRuleEvaluator.class))) @JsonView(View.Write.class) @NotNull @Valid AutomationRuleEvaluator<?> evaluator,
            @Context UriInfo uriInfo) {

        String workspaceId = requestContext.get().getWorkspaceId();
        String userName = requestContext.get().getUserName();

        UUID projectId = evaluator.getProjectId();
        log.info("Creating {} evaluator for project_id '{}' on workspace_id '{}'", evaluator.getType(),
                evaluator.getProjectId(), workspaceId);
        AutomationRuleEvaluator<?> savedEvaluator = service.save(evaluator, projectId, workspaceId, userName);
        log.info("Created {} evaluator '{}' for project_id '{}' on workspace_id '{}'", savedEvaluator.getType(),
                savedEvaluator.getId(), savedEvaluator.getProjectId(), workspaceId);

        URI uri = uriInfo.getBaseUriBuilder()
                .path("v1/private/automations/projects/{projectId}/evaluators/{id}")
                .resolveTemplate("projectId", savedEvaluator.getProjectId().toString())
                .resolveTemplate("id", savedEvaluator.getId().toString())
                .build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(operationId = "updateAutomationRuleEvaluator", summary = "Update Automation Rule Evaluator by id", description = "Update Automation Rule Evaluator by id", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
    })
    @RateLimited
    public Response updateEvaluator(@PathParam("id") UUID id,
            @RequestBody(content = @Content(schema = @Schema(implementation = AutomationRuleEvaluatorUpdate.class))) @NotNull @Valid AutomationRuleEvaluatorUpdate<?> evaluatorUpdate) {

        var workspaceId = requestContext.get().getWorkspaceId();
        var userName = requestContext.get().getUserName();

        var projectId = evaluatorUpdate.getProjectId();
        log.info("Updating automation rule evaluator by id '{}' and project_id '{}' on workspace_id '{}'", id,
                projectId, workspaceId);
        service.update(id, projectId, workspaceId, userName, evaluatorUpdate);
        log.info("Updated automation rule evaluator by id '{}' and project_id '{}' on workspace_id '{}'", id, projectId,
                workspaceId);

        return Response.noContent().build();
    }

    @POST
    @Path("/delete")
    @Operation(operationId = "deleteAutomationRuleEvaluatorBatch", summary = "Delete automation rule evaluators", description = "Delete automation rule evaluators batch", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
    })
    public Response deleteEvaluators(
            @NotNull @RequestBody(content = @Content(schema = @Schema(implementation = BatchDelete.class))) @Valid BatchDelete batchDelete,
            @QueryParam("project_id") UUID projectId) {
        String workspaceId = requestContext.get().getWorkspaceId();
        log.info("Deleting automation rule evaluators by ids, count '{}', on workspace_id '{}'",
                batchDelete.ids().size(),
                workspaceId);
        service.delete(batchDelete.ids(), projectId, workspaceId);
        log.info("Deleted automation rule evaluators by ids, count '{}', on workspace_id '{}'",
                batchDelete.ids().size(),
                workspaceId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/logs")
    @Operation(operationId = "getEvaluatorLogsById", summary = "Get automation rule evaluator logs by id", description = "Get automation rule evaluator logs by id", responses = {
            @ApiResponse(responseCode = "200", description = "Automation rule evaluator logs resource", content = @Content(schema = @Schema(implementation = LogPage.class)))
    })
    public Response getLogs(@PathParam("id") UUID evaluatorId,
            @QueryParam("size") @Min(1) @DefaultValue("1000") int size) {
        String workspaceId = requestContext.get().getWorkspaceId();

        log.info("Looking for logs for automated evaluator: id '{}' on workspace_id '{}'",
                evaluatorId, workspaceId);
        var criteria = LogCriteria.builder().workspaceId(workspaceId).entityId(evaluatorId).size(size).build();
        LogPage logs = service.getLogs(criteria).block();
        log.info("Found {} logs for automated evaluator: id '{}' on workspace_id '{}'", logs.size(),
                evaluatorId, workspaceId);

        return Response.ok().entity(logs).build();
    }

}
