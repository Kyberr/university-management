package ua.com.foxminded.university.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class TimingModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private LocalTime startTime;
    private Duration lessonDuration;
    private Duration firstBreakDuration;
    private Duration secondBreakDuration;
    private Duration thirdBreakDuration;
    private Duration fourthBreakDuration;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScheduleModel> schedules;
}
