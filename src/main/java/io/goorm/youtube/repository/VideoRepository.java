package io.goorm.youtube.repository;

import io.goorm.youtube.domain.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    //메인목록-삭제되지 않고 게시상태
    Page<Video> findByPublishYnTrueAndDeleteYnFalseOrderByRegAtDesc(Pageable pageable);


    //비디오 상세-삭제되지 않고 게시상테
    Optional<Video> findByVideoSeqAndPublishYnTrueAndDeleteYnFalse(Long videoSeq);


    //내비디오 목록
    Page<Video> findByMember_MemberSeq(Long memberSeq, Pageable pageable);

}
