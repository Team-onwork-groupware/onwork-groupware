package kr.onwork.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 근무그룹 — 소정근로 시간 단위 (work_groups). */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "work_groups")
public class WorkGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 50)
    private String groupName;

    @Column(name = "work_start_time", nullable = false)
    private LocalTime workStartTime;

    @Column(name = "work_end_time", nullable = false)
    private LocalTime workEndTime;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
}
