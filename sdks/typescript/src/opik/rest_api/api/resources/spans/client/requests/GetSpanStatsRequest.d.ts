/**
 * This file was auto-generated by Fern from our API Definition.
 */
import * as OpikApi from "../../../../index";
/**
 * @example
 *     {}
 */
export interface GetSpanStatsRequest {
    projectId?: string;
    projectName?: string;
    traceId?: string;
    type?: OpikApi.GetSpanStatsRequestType;
    filters?: string;
}