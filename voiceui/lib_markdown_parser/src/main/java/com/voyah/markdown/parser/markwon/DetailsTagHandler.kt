package com.voyah.markdown.parser.markwon

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler


/**
 *  author : jie wang
 *  date : 2024/5/15 15:05
 *  description :
 */
class DetailsTagHandler : TagHandler() {

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        var summaryEnd = -1

        for (child in tag.asBlock.children()) {
            if (!child.isClosed) {
                continue
            }
            if ("summary" == child.name()) {
                summaryEnd = child.end()
            }
            val tagHandler = renderer.tagHandler(child.name())
            if (tagHandler != null) {
                tagHandler.handle(visitor, renderer, child)
            } else if (child.isBlock) {
                visitChildren(visitor, renderer, child.asBlock)
            }
        }

        if (summaryEnd > -1) {
            visitor.builder().setSpan(
                DetailsParsingSpan(subSequenceTrimmed(visitor.builder(), tag.start(), summaryEnd)),
                tag.start(),
                tag.end()
            )
        }
    }

    override fun supportedTags(): Collection<String> {
        return setOf("details")
    }

    private fun subSequenceTrimmed(cs: CharSequence, start: Int, end: Int): CharSequence {
        var start = start
        var end = end
        while (start < end) {
            val isStartEmpty = Character.isWhitespace(cs[start])
            val isEndEmpty = Character.isWhitespace(cs[end - 1])
            if (!isStartEmpty && !isEndEmpty) {
                break
            }
            if (isStartEmpty) {
                start += 1
            }
            if (isEndEmpty) {
                end -= 1
            }
        }
        return cs.subSequence(start, end)
    }



}