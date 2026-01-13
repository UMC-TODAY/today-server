package com.example.todayserver.domain.member.entity.mapping;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Term;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "member_term",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "term_id"})
        }
)
public class MemberTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agreed", nullable = false)
    private boolean agreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;
}
