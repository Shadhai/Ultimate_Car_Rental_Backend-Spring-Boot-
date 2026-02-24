package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionImage extends BaseEntity {
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "damage_report_id")
    private DamageReport damageReport;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "image_type") // 'DAMAGE', 'PRE_RENTAL', 'POST_RENTAL'
    private String imageType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_by")
    private Long uploadedBy;
}