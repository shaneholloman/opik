# This file was auto-generated by Fern from our API Definition.

from ..core.pydantic_utilities import UniversalBaseModel
import typing
import pydantic
from .feedback_score_batch_item_source import FeedbackScoreBatchItemSource
from ..core.pydantic_utilities import IS_PYDANTIC_V2


class FeedbackScoreBatchItem(UniversalBaseModel):
    id: str
    project_name: typing.Optional[str] = pydantic.Field(default=None)
    """
    If null, the default project is used
    """

    name: str
    category_name: typing.Optional[str] = None
    value: float
    reason: typing.Optional[str] = None
    source: FeedbackScoreBatchItemSource

    if IS_PYDANTIC_V2:
        model_config: typing.ClassVar[pydantic.ConfigDict] = pydantic.ConfigDict(
            extra="allow", frozen=True
        )  # type: ignore # Pydantic v2
    else:

        class Config:
            frozen = True
            smart_union = True
            extra = pydantic.Extra.allow
