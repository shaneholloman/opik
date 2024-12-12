/**
 * This file was auto-generated by Fern from our API Definition.
 */
import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";
import { CompletionTokensDetails } from "./CompletionTokensDetails";
export declare const Usage: core.serialization.ObjectSchema<serializers.Usage.Raw, OpikApi.Usage>;
export declare namespace Usage {
    interface Raw {
        total_tokens?: number | null;
        prompt_tokens?: number | null;
        completion_tokens?: number | null;
        completion_tokens_details?: CompletionTokensDetails.Raw | null;
    }
}