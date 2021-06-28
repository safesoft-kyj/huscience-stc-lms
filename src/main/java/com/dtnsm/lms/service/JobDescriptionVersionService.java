package com.dtnsm.lms.service;

import com.dtnsm.common.entity.JobDescription;
import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.QJobDescriptionVersion;
import com.dtnsm.common.entity.constant.JobDescriptionVersionStatus;
import com.dtnsm.common.repository.JobDescriptionRepository;
import com.dtnsm.common.repository.JobDescriptionVersionRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobDescriptionVersionService {
    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobDescriptionVersionRepository repository;
    private final JobDescriptionFileService descriptionFileService;

    public List<JobDescriptionVersion> getList() {
        return repository.findAll();
    }

    public List<JobDescriptionVersion> getListByJdIdOrderByVerDesc(long jdId) {

//        return repository.findAllByJd_IdOrderByVerDesc(jdId);
        return null;
    }

    public Optional<JobDescriptionVersion> findByJobDescriptionId(Integer jobDescriptionId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QJobDescriptionVersion qJobDescriptionVersion = QJobDescriptionVersion.jobDescriptionVersion;
        booleanBuilder.and(qJobDescriptionVersion.status.eq(JobDescriptionVersionStatus.CURRENT));
        booleanBuilder.and(qJobDescriptionVersion.jobDescription.id.eq(jobDescriptionId));

        return repository.findOne(booleanBuilder);
    }

    public Optional<JobDescriptionVersion> findByJobDescriptionEqualVersion(JobDescriptionVersion jobDescriptionVersion) {
        QJobDescriptionVersion qJobDescriptionVersion = QJobDescriptionVersion.jobDescriptionVersion;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qJobDescriptionVersion.jobDescription.id.eq(jobDescriptionVersion.getJobDescription().getId()));
        builder.and(qJobDescriptionVersion.version_no.eq(jobDescriptionVersion.getVersion_no()));

        return repository.findOne(builder);
    }


    public Optional<JobDescriptionVersion> findByJobDescriptionVersion(JobDescriptionVersion jobDescriptionVersion) {
        QJobDescriptionVersion qJobDescriptionVersion = QJobDescriptionVersion.jobDescriptionVersion;

        BooleanBuilder builderTemp = new BooleanBuilder();
        builderTemp.and(qJobDescriptionVersion.status.eq(JobDescriptionVersionStatus.CURRENT));
        builderTemp.and(qJobDescriptionVersion.release_date.gt(jobDescriptionVersion.getRelease_date()));

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qJobDescriptionVersion.jobDescription.id.eq(jobDescriptionVersion.getJobDescription().getId()));
        builder.and(qJobDescriptionVersion.version_no.goe(jobDescriptionVersion.getVersion_no()).or(builderTemp));

        return repository.findOne(builder);
    }




    public JobDescriptionVersion getByJdIdAndActiveJd(long jdId) {
//        return repository.findByJd_IdAndIsActive(jdId, "1");
        return null;
    }

    public Iterable<JobDescriptionVersion> findAllVersions(Integer id) {
        QJobDescriptionVersion qJobDescriptionVersion = QJobDescriptionVersion.jobDescriptionVersion;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qJobDescriptionVersion.jobDescription.id.eq(id));
        return repository.findAll(builder, qJobDescriptionVersion.release_date.desc());
    }

    public Page<JobDescriptionVersion> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "condate"));

        return repository.findAll(pageable);
    }

    public JobDescriptionVersion getById(Integer id) {

        return repository.findById(id).get();
    }

    public JobDescriptionVersion update(JobDescriptionVersion jobDescriptionVersion) {
        return repository.save(jobDescriptionVersion);
    }

    public JobDescriptionVersion save(JobDescriptionVersion obj){
        if(ObjectUtils.isEmpty(obj.getJobDescription().getId())) {
            JobDescription savedJobDescription = jobDescriptionRepository.save(obj.getJobDescription());
            obj.setJobDescription(savedJobDescription);
        }
        JobDescriptionVersion savedJobDescriptionVersion = repository.save(obj);

        descriptionFileService.storeFile(obj.getFile(), savedJobDescriptionVersion);



        return savedJobDescriptionVersion;
    }

    public void delete(JobDescriptionVersion obj) {

        // 첨부파일 삭제
//        for(JobDescriptionVersionFile jdFile : obj.getJdFiles()) {
//            descriptionFileService.deleteFile(jdFile);
//        }

        repository.delete(obj);
    }
}
