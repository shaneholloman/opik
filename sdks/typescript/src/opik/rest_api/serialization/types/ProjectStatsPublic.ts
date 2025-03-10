/**
 * This file was auto-generated by Fern from our API Definition.
 */

import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";
import { ProjectStatItemObjectPublic } from "./ProjectStatItemObjectPublic";

export const ProjectStatsPublic: core.serialization.ObjectSchema<
    serializers.ProjectStatsPublic.Raw,
    OpikApi.ProjectStatsPublic
> = core.serialization.object({
    stats: core.serialization.list(ProjectStatItemObjectPublic).optional(),
});

export declare namespace ProjectStatsPublic {
    export interface Raw {
        stats?: ProjectStatItemObjectPublic.Raw[] | null;
    }
}
