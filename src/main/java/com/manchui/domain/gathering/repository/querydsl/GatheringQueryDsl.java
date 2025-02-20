package com.manchui.domain.gathering.repository.querydsl;

import com.manchui.domain.gathering.dto.GatheringListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GatheringQueryDsl {

    Page<GatheringListResponse> getHeartList(String email, Pageable pageable, String query, String location, String startDate, String endDate, String category, String sort, boolean available);

}
