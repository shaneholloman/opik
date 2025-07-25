import { type ClassValue, clsx } from "clsx";
import isObject from "lodash/isObject";
import isArray from "lodash/isArray";
import last from "lodash/last";
import get from "lodash/get";
import round from "lodash/round";
import isUndefined from "lodash/isUndefined";
import isNumber from "lodash/isNumber";
import times from "lodash/times";
import sample from "lodash/sample";
import mapKeys from "lodash/mapKeys";
import snakeCase from "lodash/snakeCase";
import isString from "lodash/isString";
import { twMerge } from "tailwind-merge";
import { DEFAULT_WORKSPACE_NAME } from "@/constants/user";
import { JsonNode } from "@/types/shared";

const BASE_DOCUMENTATION_URL = "https://www.comet.com/docs/opik";

export const buildDocsUrl = (path: string = "", hash: string = "") => {
  return `${BASE_DOCUMENTATION_URL}${path}?from=llm${hash}`;
};

export const isSameDomainUrl = (url: string) => {
  try {
    const resolvedUrl = new URL(url, window.location.href);
    const originUrl = window.location;

    return (
      resolvedUrl.protocol === originUrl.protocol &&
      resolvedUrl.hostname === originUrl.hostname &&
      resolvedUrl.port === originUrl.port
    );
  } catch (e) {
    return false;
  }
};

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const isStringMarkdown = (string: unknown): boolean => {
  if (!isString(string)) {
    return false;
  }

  // Return false for very short strings that are unlikely to be markdown
  if (string.length < 3) {
    return false;
  }

  // More comprehensive regex patterns for markdown detection
  const markdownPatterns = [
    // Headers (h1-h6)
    /^#{1,6}\s+.+$/m,

    // Emphasis (bold, italic, strikethrough)
    /(\*\*|__).+?(\*\*|__)/, // bold
    /(\*|_).+?(\*|_)/, // italic
    /~~.+?~~/, // strikethrough

    // Links and images
    /\[.+?\]\(.+?\)/, // links
    /!\[.+?\]\(.+?\)/, // images

    // Lists
    /^(\s*[*\-+]\s+.+)$/m, // unordered lists
    /^(\s*\d+\.\s+.+)$/m, // ordered lists

    // Blockquotes
    /^>\s+.+$/m, // blockquotes

    // Code
    /```[\s\S]*?```/, // code blocks
    /`[^`]+`/, // inline code

    // Tables
    /^\|.+\|\s*$/m, // table rows
    /^[|\-:\s]+$/m, // table separators

    // Horizontal rules
    /^(\*{3,}|-{3,}|_{3,})$/m, // hr

    // Task lists
    /^\s*[*\-+]\s+\[[ xX]]\s+.+$/m, // task lists

    // Definition lists
    /^.+?\n:\s+.+$/m, // definition lists

    // Footnote references and definitions
    /\[\^.+?]/, // footnote references
    /^\[\^.+?]:/m, // footnote definitions
  ];

  // Check for markdown patterns
  return markdownPatterns.some((pattern) => pattern.test(string));
};

export const isValidJsonObject = (string: string) => {
  let json = null;
  try {
    json = JSON.parse(string);
  } catch (e) {
    return false;
  }

  return json && isObject(json);
};

export const safelyParseJSON = (string: string, silent = false) => {
  try {
    return JSON.parse(string);
  } catch (e) {
    if (!silent) console.error(e);
    return {};
  }
};

export const getJSONPaths = (
  node: JsonNode,
  previousPath: string = "",
  results: string[] = [],
) => {
  if (isObject(node) || isArray(node)) {
    for (const key in node) {
      const value = get(node, key);
      const path = previousPath
        ? isArray(node)
          ? `${previousPath}[${key}]`
          : `${previousPath}.${key}`
        : key;

      if (isArray(value)) {
        getJSONPaths(value, path, results);
      } else if (isObject(value)) {
        getJSONPaths(value, path, results);
      } else {
        results.push(path);
      }
    }
  }

  return results;
};

export const getTextWidth = (
  text: string[],
  properties: {
    font?: string;
  } = {},
) => {
  const canvas = document.createElement("canvas");
  const context = canvas.getContext("2d") as CanvasRenderingContext2D;

  if (properties.font) {
    context.font = properties.font;
  }

  return text.map((v) => context.measureText(v).width);
};

export const toString = (value?: string | number | boolean | null) =>
  isUndefined(value) ? "" : String(value);

export const maskAPIKey = (apiKey: string = "") =>
  `${apiKey.substring(0, 6)}*****************`;

export const generateRandomString = (length: number = 6): string => {
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  return times(length, () => sample(characters)).join("");
};

export const getAlphabetLetter = (i: number) => {
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  return characters.charAt(i % characters.length);
};

export const snakeCaseObj = <T extends object>(obj: T) => {
  return mapKeys(obj, (_, key) => snakeCase(key));
};

export const calculateWorkspaceName = (
  workspaceName: string,
  defaultName = "Personal",
) => (workspaceName === DEFAULT_WORKSPACE_NAME ? defaultName : workspaceName);

export const extractIdFromLocation = (location: string) =>
  last(location?.split("/"));

export const formatNumericData = (value: number, precision = 3) =>
  String(round(value, precision));

export const formatNumberInK = (value: number, precision = 1): string => {
  const ranges = [
    { threshold: 1000000000, suffix: "B", divider: 1000000000 },
    { threshold: 1000000, suffix: "M", divider: 1000000 },
    { threshold: 1000, suffix: "K", divider: 1000 },
  ];

  const range = ranges.find((r) => value >= r.threshold);

  return range
    ? `${(value / range.divider).toFixed(precision)}${range.suffix}`
    : value.toString();
};

export const calculatePercentageChange = (
  baseValue: number | null | undefined,
  newValue: number | null | undefined,
): number | undefined => {
  if (!isNumber(baseValue) || !isNumber(newValue)) return undefined;
  if (baseValue === 0 && newValue === 0) return 0;
  if (baseValue === 0) return undefined;
  return ((newValue - baseValue) / Math.abs(baseValue)) * 100;
};

export const updateTextAreaHeight = (
  textarea: HTMLTextAreaElement | null,
  minHeight: number = 80,
) => {
  if (!textarea) return;

  const BORDER_WIDTH = 1;

  textarea.style.height = `${minHeight}px`;
  const scrollHeight = textarea.scrollHeight + BORDER_WIDTH * 2;

  textarea.style.height = scrollHeight + "px";
};

export const capitalizeFirstLetter = (str?: string | null) =>
  str ? str.charAt(0).toUpperCase() + str.slice(1) : "";
