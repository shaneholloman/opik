import React, { useMemo } from "react";
import { Span, Trace } from "@/types/traces";
import { processInputData } from "@/lib/images";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import SyntaxHighlighter from "@/components/shared/SyntaxHighlighter/SyntaxHighlighter";
import AttachmentsList from "./AttachmentsList";
import Loader from "@/components/shared/Loader/Loader";

type InputOutputTabProps = {
  data: Trace | Span;
  isLoading: boolean;
  search?: string;
};

const InputOutputTab: React.FunctionComponent<InputOutputTabProps> = ({
  data,
  isLoading,
  search,
}) => {
  const { images, formattedData } = useMemo(
    () => processInputData(data.input),
    [data.input],
  );
  const hasError = Boolean(data.error_info);

  return (
    <Accordion
      type="multiple"
      className="w-full"
      defaultValue={["attachments", "error", "input", "output"]}
    >
      <AttachmentsList data={data} images={images} />
      {hasError && (
        <AccordionItem className="group" value="error" disabled={isLoading}>
          <AccordionTrigger>Error</AccordionTrigger>
          <AccordionContent
            forceMount
            className="group-data-[state=closed]:hidden"
          >
            {isLoading ? (
              <Loader />
            ) : (
              <SyntaxHighlighter
                data={data.error_info!}
                preserveKey="syntax-highlighter-trace-sidebar-error"
                withSearch
                search={search}
              />
            )}
          </AccordionContent>
        </AccordionItem>
      )}
      <AccordionItem className="group" value="input" disabled={isLoading}>
        <AccordionTrigger>Input</AccordionTrigger>
        <AccordionContent
          forceMount
          className="group-data-[state=closed]:hidden"
        >
          {isLoading ? (
            <Loader />
          ) : (
            <SyntaxHighlighter
              data={formattedData as object}
              prettifyConfig={{ fieldType: "input" }}
              preserveKey="syntax-highlighter-trace-sidebar-input"
              search={search}
              withSearch
            />
          )}
        </AccordionContent>
      </AccordionItem>
      <AccordionItem className="group" value="output" disabled={isLoading}>
        <AccordionTrigger>Output</AccordionTrigger>
        <AccordionContent
          forceMount
          className="group-data-[state=closed]:hidden"
        >
          {isLoading ? (
            <Loader />
          ) : (
            <SyntaxHighlighter
              data={data.output}
              prettifyConfig={{ fieldType: "output" }}
              preserveKey="syntax-highlighter-trace-sidebar-output"
              search={search}
              withSearch
            />
          )}
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
};

export default InputOutputTab;
