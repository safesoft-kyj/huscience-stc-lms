package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "el_cv_team_dept")
@ToString(exclude = "careerHistory")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
public class CVTeamDept extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = 3231735123894893487L;
    @Id
    @SequenceGenerator(name = "CV_CAREER_HISTORY_TEAM_DEPT_SEQ_GENERATOR", sequenceName = "SEQ_CV_CAREER_HISTORY_TEAM_DEPT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_CAREER_HISTORY_TEAM_DEPT_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_career_history_id", referencedColumnName = "id")
    private CVCareerHistory careerHistory;

    @Column(name = "position", columnDefinition = "nvarchar(500)")
    private String position;

    @Column(name = "team", columnDefinition = "nvarchar(500)")
    private String team;

    @Column(name = "department", columnDefinition = "nvarchar(500)")
    private String department;
}
