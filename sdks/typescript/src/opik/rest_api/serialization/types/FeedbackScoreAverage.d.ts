/**
 * This file was auto-generated by Fern from our API Definition.
 */
import * as serializers from "../index";
import * as OpikApi from "../../api/index";
import * as core from "../../core";
export declare const FeedbackScoreAverage: core.serialization.ObjectSchema<serializers.FeedbackScoreAverage.Raw, OpikApi.FeedbackScoreAverage>;
export declare namespace FeedbackScoreAverage {
    interface Raw {
        name: string;
        value: number;
    }
}