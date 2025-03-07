# This file was auto-generated by Fern from our API Definition.

from ..core.pydantic_utilities import UniversalBaseModel
import typing
from .json_schema_element import JsonSchemaElement
import typing_extensions
from ..core.serialization import FieldMetadata
from ..core.pydantic_utilities import IS_PYDANTIC_V2
import pydantic


class JsonObjectSchema(UniversalBaseModel):
    type: typing.Optional[str] = None
    description: typing.Optional[str] = None
    properties: typing.Optional[typing.Dict[str, JsonSchemaElement]] = None
    required: typing.Optional[typing.List[str]] = None
    additional_properties: typing_extensions.Annotated[
        typing.Optional[bool], FieldMetadata(alias="additionalProperties")
    ] = None
    defs: typing_extensions.Annotated[
        typing.Optional[typing.Dict[str, JsonSchemaElement]],
        FieldMetadata(alias="$defs"),
    ] = None

    if IS_PYDANTIC_V2:
        model_config: typing.ClassVar[pydantic.ConfigDict] = pydantic.ConfigDict(
            extra="allow", frozen=True
        )  # type: ignore # Pydantic v2
    else:

        class Config:
            frozen = True
            smart_union = True
            extra = pydantic.Extra.allow
