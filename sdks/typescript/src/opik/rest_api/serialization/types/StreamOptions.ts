/**
 * This file was auto-generated by Fern from our API Definition.
 */

import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";

export const StreamOptions: core.serialization.ObjectSchema<serializers.StreamOptions.Raw, OpikApi.StreamOptions> =
    core.serialization.object({
        includeUsage: core.serialization.property("include_usage", core.serialization.boolean().optional()),
    });

export declare namespace StreamOptions {
    interface Raw {
        include_usage?: boolean | null;
    }
}