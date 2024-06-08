package com.example.practice1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ManageReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<String> reportList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_report, container, false);

        // RecyclerView 초기화
        recyclerView = rootView.findViewById(R.id.recyclerView_my_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter();
        recyclerView.setAdapter(adapter);

        // 데이터 초기화
        reportList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            reportList.add("아이템 " + (i + 1));
        }
        adapter.notifyDataSetChanged();

        return rootView;
    }

    // RecyclerView 어댑터 클래스
    private class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(reportList.get(position));
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        // ViewHolder 클래스
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.title);
            }
        }
    }
}
