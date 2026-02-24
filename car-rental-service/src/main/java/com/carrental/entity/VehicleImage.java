package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleImage extends BaseEntity {
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle; // This should match the mappedBy in Vehicle

    @Column(name = "is_primary")
    private boolean primary = false;

    @Column(name = "image_order")
    private Integer order;

    @Column(name = "caption")
    private String caption;
}