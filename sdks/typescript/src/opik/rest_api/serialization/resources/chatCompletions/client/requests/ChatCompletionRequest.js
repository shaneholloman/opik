"use strict";
/**
 * This file was auto-generated by Fern from our API Definition.
 */
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.ChatCompletionRequest = void 0;
const core = __importStar(require("../../../../../core"));
const Message_1 = require("../../../../types/Message");
const StreamOptions_1 = require("../../../../types/StreamOptions");
const ResponseFormat_1 = require("../../../../types/ResponseFormat");
const Tool_1 = require("../../../../types/Tool");
const Function_1 = require("../../../../types/Function");
const FunctionCall_1 = require("../../../../types/FunctionCall");
exports.ChatCompletionRequest = core.serialization.object({
    model: core.serialization.string().optional(),
    messages: core.serialization.list(Message_1.Message).optional(),
    temperature: core.serialization.number().optional(),
    topP: core.serialization.property("top_p", core.serialization.number().optional()),
    n: core.serialization.number().optional(),
    stream: core.serialization.boolean().optional(),
    streamOptions: core.serialization.property("stream_options", StreamOptions_1.StreamOptions.optional()),
    stop: core.serialization.list(core.serialization.string()).optional(),
    maxTokens: core.serialization.property("max_tokens", core.serialization.number().optional()),
    maxCompletionTokens: core.serialization.property("max_completion_tokens", core.serialization.number().optional()),
    presencePenalty: core.serialization.property("presence_penalty", core.serialization.number().optional()),
    frequencyPenalty: core.serialization.property("frequency_penalty", core.serialization.number().optional()),
    logitBias: core.serialization.property("logit_bias", core.serialization.record(core.serialization.string(), core.serialization.number()).optional()),
    user: core.serialization.string().optional(),
    responseFormat: core.serialization.property("response_format", ResponseFormat_1.ResponseFormat.optional()),
    seed: core.serialization.number().optional(),
    tools: core.serialization.list(Tool_1.Tool).optional(),
    toolChoice: core.serialization.property("tool_choice", core.serialization.record(core.serialization.string(), core.serialization.unknown()).optional()),
    parallelToolCalls: core.serialization.property("parallel_tool_calls", core.serialization.boolean().optional()),
    functions: core.serialization.list(Function_1.Function).optional(),
    functionCall: core.serialization.property("function_call", FunctionCall_1.FunctionCall.optional()),
});