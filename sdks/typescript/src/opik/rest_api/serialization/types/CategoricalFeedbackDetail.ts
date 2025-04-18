/**
 * This file was auto-generated by Fern from our API Definition.
 */

import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";

export const CategoricalFeedbackDetail: core.serialization.ObjectSchema<
    serializers.CategoricalFeedbackDetail.Raw,
    OpikApi.CategoricalFeedbackDetail
> = core.serialization.object({
    categories: core.serialization.record(core.serialization.string(), core.serialization.number()),
});

export declare namespace CategoricalFeedbackDetail {
    export interface Raw {
        categories: Record<string, number>;
    }
}
