package com.voyah.markdown.parser.markwon

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.voyah.markdown.parser.R
import com.voyah.voice.framework.helper.AppHelper
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.RenderProps
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.SpanFactory
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.utils.Dip
import org.commonmark.node.BulletList
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.Paragraph


/**
 *  author : jie wang
 *  date : 2024/5/15 15:19
 *  description :
 */
class MarkwonHolder {

    private val context: Context = AppHelper.getApplication()

    val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon.builder(context)
            .usePlugin(HtmlPlugin.create { plugin ->
                plugin.addHandler(
                    DetailsTagHandler()
                )
            })
//            .usePlugin(ImagesPlugin.create())
            .usePlugin(HeadingColorPlugin(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create { builder ->
                val dip: Dip = Dip.create(context)
                val colorBorder = context.resources.getColor(R.color.markdown_table_border)
                val colorCellHeader = context.resources.getColor(R.color.markdown_cell_header)
                val colorCellEven = context.resources.getColor(R.color.markdown_root_view_bg)
                builder
                    .tableBorderWidth(dip.toPx(2))
                    .tableBorderColor(colorBorder)
                    .tableCellPadding(dip.toPx(10))
                    .tableHeaderRowBackgroundColor(colorCellHeader)
                    .tableEvenRowBackgroundColor(colorCellEven)
                    .tableOddRowBackgroundColor(colorCellEven)
            })
            .usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    LogUtils.d("load glide plugin load image...")
                    return Glide.with(context)
                        .load(drawable.destination)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_home_black_36dp)
                }

                override fun cancel(target: Target<*>) {
                    LogUtils.d("cancel glide plugin cancel load...")
                    Glide.with(context)
                        .clear(target)
                }

            }))
            .build()
    }

    companion object {
        fun getInstance(): MarkwonHolder = Holder.instance
    }

    private object Holder {
        val instance = MarkwonHolder()
    }

    fun getMarkWonInstance(completionBlock: () -> Unit): Markwon {
        return Markwon.builder(context)
            .usePlugin(HtmlPlugin.create { plugin ->
                plugin.addHandler(
                    DetailsTagHandler()
                )
            })
            .usePlugin(HeadingColorPlugin(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(TablePlugin.create { builder ->
                LogUtils.d("configureTheme table plugin.")
                val dip: Dip = Dip.create(context)
                val colorBorder = context.resources.getColor(R.color.markdown_table_border)
                val colorCellHeader = context.resources.getColor(R.color.markdown_cell_header)
                val colorCellEven = context.resources.getColor(R.color.markdown_root_view_bg)
                builder
                    .tableBorderWidth(dip.toPx(2))
                    .tableBorderColor(colorBorder)
                    .tableCellPadding(dip.toPx(10))
                    .tableHeaderRowBackgroundColor(colorCellHeader)
                    .tableEvenRowBackgroundColor(colorCellEven)
                    .tableOddRowBackgroundColor(colorCellEven)
            })
            .usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    LogUtils.d("load glide plugin load image...")
                    val placeHolder =
                        ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp)
//                    placeHolder?.setBounds(0, 0, 0, 0)
                    return Glide.with(context)
                        .load(drawable.destination)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(placeHolder)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                LogUtils.i("onLoadFailed e:$e")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                LogUtils.d("onResource ready...")
                                completionBlock.invoke()
                                return false
                            }

                        })
                }

                override fun cancel(target: Target<*>) {
                    LogUtils.d("cancel glide plugin cancel load...")
                    Glide.with(context).clear(target)
                }

            }))
            .build()
    }
}

class HeadingColorPlugin(val context: Context): AbstractMarkwonPlugin() {

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        super.configureSpansFactory(builder)
        builder.prependFactory(Heading::class.java) { configuration: MarkwonConfiguration?,
                                                      props: RenderProps? ->
            // here you can also inspect heading level
            val colorHeadingLevel = context.resources.getColor(R.color.markdown_heading_level)
            val level = CoreProps.HEADING_LEVEL.require(props!!)
            val color: Int = if (level == 1) {
                colorHeadingLevel
            } else if (level == 2) {
                colorHeadingLevel
            } else {
                colorHeadingLevel
            }
            ForegroundColorSpan(color)
        }

        builder.appendFactory(BulletList::class.java) { configuration: MarkwonConfiguration?,
                                                      props: RenderProps? ->
            val bullet = LeadingMarginSpan.Standard(48, 0)
            bullet
        }
    }

    override fun configureTheme(builder: MarkwonTheme.Builder) {
//        var themeNew : MarkwonTheme = configuration?.theme()!!
        builder.headingTextSizeMultipliers(floatArrayOf(1.5f, 1.3f, 1.143f, 1.0f, 0.8f, 0.6f))
//        builder.listItemColor(context.getColor(R.color.red))
        CoreProps.LIST_ITEM_TYPE
        super.configureTheme(builder)
    }

}