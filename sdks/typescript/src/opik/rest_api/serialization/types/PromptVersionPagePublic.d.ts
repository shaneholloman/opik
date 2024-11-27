/**
 * This file was auto-generated by Fern from our API Definition.
 */
import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";
import { PromptVersionPublic } from "./PromptVersionPublic";
export declare const PromptVersionPagePublic: core.serialization.ObjectSchema<serializers.PromptVersionPagePublic.Raw, OpikApi.PromptVersionPagePublic>;
export declare namespace PromptVersionPagePublic {
    interface Raw {
        page?: number | null;
        size?: number | null;
        total?: number | null;
        content?: PromptVersionPublic.Raw[] | null;
    }
}