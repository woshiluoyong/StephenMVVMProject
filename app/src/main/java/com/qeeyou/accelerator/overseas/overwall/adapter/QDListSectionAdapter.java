package com.qeeyou.accelerator.overseas.overwall.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.puy.mvvm.utils.QMUIViewHelper;
import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;
import com.qeeyou.accelerator.overseas.overwall.R;
import com.qeeyou.accelerator.overseas.overwall.beans.MainAccInfoBean;
import com.qeeyou.accelerator.overseas.overwall.entitys.SectionHeader;
import com.qeeyou.accelerator.overseas.overwall.entitys.SectionItem;
import com.qeeyou.accelerator.overseas.overwall.qmui.QMUIDefaultStickySectionAdapter;
import com.qeeyou.accelerator.overseas.overwall.qmui.QMUISection;
import com.qeeyou.accelerator.overseas.overwall.qmui.QMUIStickySectionAdapter;
import com.qeeyou.accelerator.overseas.overwall.utils.Constant;
import com.qeeyou.accelerator.overseas.overwall.utils.ToolUtils;
import com.qeeyou.accelerator.overseas.overwall.widgets.QDLoadingItemView;
import com.qeeyou.accelerator.overseas.overwall.widgets.StephenRadioView;
import com.shacharsoftware.rtlmarqueeview.RtlMarqueeView;

import java.util.List;

public class QDListSectionAdapter extends QMUIDefaultStickySectionAdapter<SectionHeader, SectionItem> {
    private Activity activity;
    private OnLineListClickListener onLineListClickListener;

    public QDListSectionAdapter(Activity activity, OnLineListClickListener onLineListClickListener) {
        this.activity = activity;
        this.onLineListClickListener = onLineListClickListener;
    }

    @NonNull
    @Override
    protected QMUIStickySectionAdapter.ViewHolder onCreateSectionHeaderViewHolder(@NonNull ViewGroup viewGroup) {
        return new QMUIStickySectionAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_line_header, null));
    }

    @NonNull
    @Override
    protected QMUIStickySectionAdapter.ViewHolder onCreateSectionItemViewHolder(@NonNull ViewGroup viewGroup) {
        return new QMUIStickySectionAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_line_list, null));
    }

    @NonNull
    @Override
    protected QMUIStickySectionAdapter.ViewHolder onCreateSectionLoadingViewHolder(@NonNull ViewGroup viewGroup) {
        return new ViewHolder(new QDLoadingItemView(viewGroup.getContext()));
    }

    @Override
    protected void onBindSectionHeader(final ViewHolder holder, final int position, QMUISection<SectionHeader, SectionItem> section) {
        boolean isAutoAccHeader = SectionHeader.AUTO_ACC.equals(section.getHeader().getText());
        View mainAutoV = holder.itemView.findViewById(R.id.mainAutoV);
        View mainLineV = holder.itemView.findViewById(R.id.mainLineV);
        ToolUtils.getInstance().setViewVisibility(isAutoAccHeader, mainAutoV);
        ToolUtils.getInstance().setViewVisibility(!isAutoAccHeader, mainLineV);
        StephenRadioView selectRadioV = holder.itemView.findViewById(R.id.selectRadioV);
        selectRadioV.resetSetWidthHeight(ToolUtils.getInstance().dp2px(activity, 24), ToolUtils.getInstance().dp2px(activity, 24));
        QMUIViewHelper.expendTouchArea(selectRadioV, 30);
        selectRadioV.changeRadioSelect(!Hawk.contains(Constant.USER_ACC_HAND_MODE));
        if(isAutoAccHeader){
            ((ImageView)holder.itemView.findViewById(R.id.expandImgV)).setImageResource(section.isFold() ? R.drawable.icon_arrow_right : R.drawable.icon_arrow_up);
            mainLineV.setOnClickListener(null);
            mainAutoV.setOnClickListener(v -> onLineListClickListener.lineListClickEvent(null));
        }else{
            Glide.with(activity).load(section.getHeader().getImgUrl()).error(R.drawable.pic_placeholder_error_shape)
                    .placeholder(R.drawable.pic_placeholder_error_shape).into((ImageView)holder.itemView.findViewById(R.id.lineLogoImgV));
            ((TextView)holder.itemView.findViewById(R.id.lineNameT)).setText(section.getHeader().getText());
            ((ImageView)holder.itemView.findViewById(R.id.expandImgV)).setImageResource(section.isFold() ? R.drawable.icon_arrow_right : R.drawable.icon_arrow_up);
            mainAutoV.setOnClickListener(null);
            mainLineV.setOnClickListener(v -> {
                int pos = holder.isForStickyHeader ? position : holder.getAdapterPosition();
                toggleFold(pos, false);
            });
        }
    }

    @Override
    protected void onBindSectionItem(ViewHolder holder, int position, QMUISection<SectionHeader, SectionItem> section, int itemIndex) {
        MainAccInfoBean.Data.Node sectionNodeItem = section.getItemAt(itemIndex).getMainBeanData();

        holder.itemView.setTag(sectionNodeItem);
        RtlMarqueeView lineNameT = ((RtlMarqueeView)holder.itemView.findViewById(R.id.lineNameT));
        lineNameT.setText(sectionNodeItem.getName());

        TextView lineSpeedT = holder.itemView.findViewById(R.id.lineSpeedT);
        View pingLoadingV = holder.itemView.findViewById(R.id.pingLoadingV);
        ToolUtils.getInstance().setViewVisibility(false, pingLoadingV);
        ToolUtils.getInstance().setViewVisibility(false, lineSpeedT);
        StephenRadioView selectRadioV = holder.itemView.findViewById(R.id.selectRadioV);
        QMUIViewHelper.expendTouchArea(selectRadioV, 30);
        selectRadioV.changeRadioSelect(sectionNodeItem.is_selected());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        View mainItemV = holder.itemView.findViewById(R.id.mainItemV);
        if(null != mainItemV)mainItemV.setOnClickListener(v -> {
            MainAccInfoBean.Data.Node nodeInfo = (null != v && null != v.getTag() && v.getTag() instanceof MainAccInfoBean.Data.Node) ? (MainAccInfoBean.Data.Node)v.getTag() : null;
            if(null != nodeInfo){
                onLineListClickListener.lineListClickEvent(nodeInfo);
            }else{
                ToolUtils.getInstance().showShortHintInfo(activity, "Data error, unable to operate!");
            }
        });
    }

    public interface OnLineListClickListener{
        void lineListClickEvent(MainAccInfoBean.Data.Node node);
    }
}
