package ua.com.foxminded.university.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "timetables", schema = "university")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Timetable implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    
    @OneToMany(mappedBy = "timetable")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Lesson> lessons;
    
    @OneToMany(mappedBy = "timetable", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Timing> timings;
    
    public void addTiming(Timing timing) {
        this.timings.add(timing);
        timing.setTimetable(this);
    }
    
    public void removeTiming(Timing timing) {
        this.timings.remove(timing);
        timing.setTimetable(null);
    }
}
