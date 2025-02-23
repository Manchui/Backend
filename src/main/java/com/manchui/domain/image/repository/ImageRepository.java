package com.manchui.domain.image.repository;

import com.manchui.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByGatheringId(Long gatheringId);

}
