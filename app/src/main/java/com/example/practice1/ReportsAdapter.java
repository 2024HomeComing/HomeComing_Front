package com.example.practice1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.Report;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private OnReportClickListener onReportClickListener;

    public interface OnReportClickListener {
        void onReportClick(Report report);
    }

    public ReportsAdapter(List<Report> reportList, OnReportClickListener onReportClickListener) {
        this.reportList = reportList;
        this.onReportClickListener = onReportClickListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);
        holder.itemView.setOnClickListener(v -> onReportClickListener.onReportClick(report));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReportInfo;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReportInfo = itemView.findViewById(R.id.textViewReportInfo);
        }

        void bind(Report report) {
            textViewReportInfo.setText("ID: " + report.getId() + ", 날짜: " + report.getReportDate());
        }
    }
}
