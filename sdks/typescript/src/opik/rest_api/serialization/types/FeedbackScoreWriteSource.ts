/**
 * This file was auto-generated by Fern from our API Definition.
 */

import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";

export const FeedbackScoreWriteSource: core.serialization.Schema<
    serializers.FeedbackScoreWriteSource.Raw,
    OpikApi.FeedbackScoreWriteSource
> = core.serialization.enum_(["ui", "sdk", "online_scoring"]);

export declare namespace FeedbackScoreWriteSource {
    export type Raw = "ui" | "sdk" | "online_scoring";
}
