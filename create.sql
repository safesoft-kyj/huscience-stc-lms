create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table binder_jd (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10), signature varchar(100), signature2 varchar(100), user_id VARCHAR(30), version_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, reg_date varchar(10) not null, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, ver double precision not null, jd_id bigint, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, jd_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table binder_jd add constraint FK1ak137i8to7tkghiq8iv8cg80 foreign key (user_id) references account
alter table binder_jd add constraint FKmw6wtl75xa4r1471usewn9a6q foreign key (version_id) references job_description_version
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version_file add constraint FKxvwobmj6dg4bdr57pmyjkur4 foreign key (jd_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table binder_jd (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10), signature varchar(100), signature2 varchar(100), user_id VARCHAR(30), version_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, reg_date varchar(10) not null, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, ver double precision not null, jd_id bigint, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, jd_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table binder_jd add constraint FK1ak137i8to7tkghiq8iv8cg80 foreign key (user_id) references account
alter table binder_jd add constraint FKmw6wtl75xa4r1471usewn9a6q foreign key (version_id) references job_description_version
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version_file add constraint FKxvwobmj6dg4bdr57pmyjkur4 foreign key (jd_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, active bit not null, release_date datetime2, version_no varchar(255), jd_id bigint, job_description_version_file_id int, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, job_description_version_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table job_description add constraint UKngfovmg54i475m02u0adw69la unique (short_name)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version add constraint FKp7uunxfqe7ep48xwptgaoh7gq foreign key (job_description_version_file_id) references job_description_version_file
alter table job_description_version_file add constraint FKjc9yctk3do6s713vfy1hd2jme foreign key (job_description_version_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, active bit not null, release_date datetime2, version_no varchar(255), jd_id bigint, job_description_version_file_id int, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, job_description_version_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table job_description add constraint UKngfovmg54i475m02u0adw69la unique (short_name)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version add constraint FKp7uunxfqe7ep48xwptgaoh7gq foreign key (job_description_version_file_id) references job_description_version_file
alter table job_description_version_file add constraint FKjc9yctk3do6s713vfy1hd2jme foreign key (job_description_version_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, active bit not null, release_date datetime2, version_no varchar(255), jd_id bigint, job_description_version_file_id int, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, job_description_version_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table job_description add constraint UKngfovmg54i475m02u0adw69la unique (short_name)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version add constraint FKp7uunxfqe7ep48xwptgaoh7gq foreign key (job_description_version_file_id) references job_description_version_file
alter table job_description_version_file add constraint FKjc9yctk3do6s713vfy1hd2jme foreign key (job_description_version_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
create sequence hibernate_sequence start with 1 increment by 1
create table account (user_id varchar(30) not null, com_job varchar(255), com_position varchar(255), email varchar(255), enabled bit not null, eng_name varchar(255), indate varchar(255), name varchar(255), org_depart varchar(255), parent_user_id varchar(30) default 'N', password varchar(255), token_expired bit not null, user_type varchar(255), primary key (user_id))
create table account_roles (user_id varchar(30) not null, role_id bigint not null)
create table binder_cv (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, is_active varchar(255), reg_date varchar(10) not null, signature varchar(100), signature2 varchar(100), ver double precision not null, user_id VARCHAR(30), primary key (id))
create table binder_cv_career_history (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, team_department varchar(255), company varchar(255), company_address varchar(255), period varchar(255), position varchar(255), cv_id bigint, primary key (id))
create table binder_cv_education (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, degree varchar(255), edu_period varchar(255), institute varchar(255), institute_address varchar(255), thesis_option varchar(255), thesis_title varchar(255), cv_id bigint, primary key (id))
create table binder_cv_experience (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content varchar(2000), name varchar(255), cv_id bigint, primary key (id))
create table binder_cv_licenses_certification (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, certifications varchar(2000), licenses varchar(2000), cv_id bigint, primary key (id))
create table binder_cv_professional_affiliations (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, membership varchar(255), cv_id bigint, primary key (id))
create table binder_cv_skills (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, computer_knowledge varchar(255), computer_knowledge_level varchar(255), languages varchar(255), languages_level varchar(255), cv_id bigint, primary key (id))
create table el_border (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, content TEXT, from_date varchar(10), is_notice varchar(1) default 0, reply_cnt int default 0 not null, title varchar(100) not null, to_date varchar(10), view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_border_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, border_id bigint, primary key (id))
create table el_border_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, border_name varchar(100) not null, is_mail varchar(1) not null, minor_cd varchar(10) not null, primary key (id))
create table el_course (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, active int default 0 not null, agenda TEXT, amount int default 0 not null, certi_head varchar(20), cnt int default 0 not null, content TEXT, day int default 0 not null, from_date varchar(10) default 1900-01-01, hour int default 0 not null, is_book varchar(255), is_certi varchar(255), is_quiz varchar(255), is_survey varchar(255), mail_sender varchar(1000), place varchar(50), request_from_date varchar(10) default 1900-01-01, request_to_date varchar(10) default 1900-01-01, status int default 0 not null, team varchar(30), title varchar(100) not null, to_date varchar(10) default 1900-01-01, view_cnt int default 0 not null, type_id varchar(10), primary key (id))
create table el_course_account (user_id VARCHAR(30) not null, course_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, course_id))
create table el_course_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), name varchar(255), save_name varchar(255), size bigint, course_id bigint, primary key (id))
create table el_course_manager (user_id varchar(30) not null, insert_user_id varchar(30), insert_dt datetime2, register_date varchar(10), primary key (user_id))
create table el_course_master (id varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, course_name varchar(100) not null, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), minor_cd varchar(10) not null, request_type varchar(1) default 1 not null, primary key (id))
create table el_course_quiz (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), exam_minute int default 0, name varchar(255), path_count int, exam_second int default 0, to_date varchar(10), course_id bigint, primary key (id))
create table el_course_quiz_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), quiz_id bigint, primary key (id))
create table el_course_quiz_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), answer_count int not null, user_answer varchar(1), question_id bigint, quiz_action_id bigint, primary key (id))
create table el_course_quiz_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, quiz_action_id bigint, primary key (id))
create table el_course_quiz_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, quiz_id bigint, primary key (id))
create table el_course_quiz_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, answer varchar(1), ex1 varchar(500), ex2 varchar(500), ex3 varchar(500), ex4 varchar(500), ex5 varchar(500), ex6 varchar(500), ex7 varchar(500), ex8 varchar(500), ex9 varchar(500), question varchar(1000), quiz_id bigint, primary key (id))
create table el_course_section (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, description varchar(1000), from_date varchar(10) default 1900-01-01, from_time varchar(8), section_minute int default 0, name varchar(255), section_second int default 0, teacher varchar(255), to_date varchar(10) default 1900-01-01, to_time varchar(8), course_id bigint, type varchar(10), primary key (id))
create table el_course_section_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), run_count int, score int, status varchar(10) default 0, total_use_second int default 0, user_id varchar(30), section_id bigint, primary key (id))
create table el_course_section_action_history (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, end_date datetime2, start_date datetime2, use_second int, section_action_id bigint, primary key (id))
create table el_course_section_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, section_id bigint, primary key (id))
create table el_course_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, from_date varchar(10), name varchar(255), to_date varchar(10), course_id bigint, survey_id bigint, primary key (id))
create table el_course_survey_action (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, exam_date varchar(10), score int, status varchar(10) default 0, user_id varchar(30), course_survey_id bigint, primary key (id))
create table el_course_survey_action_answer (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, survey_gubun varchar(1), user_answer varchar(500), question_id bigint, survey_action_id bigint, primary key (id))
create table el_course_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), course_survey_id bigint, primary key (id))
create table el_customer (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, condate varchar(10) not null, condition varchar(50) not null, homepage varchar(100) not null, name varchar(50) not null, view_cnt int default 0 not null, primary key (id))
create table el_document (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, mail_sender varchar(1000), title varchar(100) not null, view_cnt int default 0 not null, user_id varchar(30), template_id int, primary key (id))
create table el_document_account (user_id VARCHAR(30) not null, document_id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, appr_date1 varchar(10), appr_date2 varchar(10), appr_date_time1 datetime2, appr_date_time2 datetime2, is_appr1 varchar(1), is_appr2 varchar(1), is_commit varchar(255), is_course_manger_approval varchar(1), is_team_manger_approval varchar(1), request_date varchar(10), request_type varchar(10) default 0, status varchar(30) default 0, appr_user_id1 VARCHAR(30), appr_user_id2 VARCHAR(30), primary key (user_id, document_id))
create table el_document_course_account (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, user_id VARCHAR(30), document_id bigint, primary key (id))
create table el_document_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, doc_id bigint, primary key (id))
create table el_document_template (id int not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, is_course_manger_approval varchar(255), is_team_manger_approval varchar(255), title varchar(100) not null, primary key (id))
create table el_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, primary key (id))
create table el_major (major_cd varchar(5) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, major_nm varchar(30) not null, type varchar(1) not null, primary key (major_cd))
create table el_minor (minor_cd varchar(10) not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, minor_nm varchar(30) not null, seq int default 1 not null, major_cd varchar(5) not null, primary key (minor_cd))
create table el_munje_bank (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(200), ex2 varchar(200), ex3 varchar(200), ex4 varchar(200), ex5 varchar(200), is_ans1 varchar(1) default 0, is_ans2 varchar(1) default 0, is_ans3 varchar(1) default 0, is_ans4 varchar(1) default 0, is_ans5 varchar(1) default 0, munje_gubun varchar(30), munje_type varchar(30), quest varchar(500), primary key (id))
create table el_schedule (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, content TEXT, sctype varchar(10), title varchar(100) not null, view_cnt int default 0 not null, year varchar(4), primary key (id))
create table el_schedule_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, schedule_id bigint, primary key (id))
create table el_survey (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, name varchar(255), primary key (id))
create table el_survey_file (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255), mime_type varchar(255), save_name varchar(255), size bigint, survey_id bigint, primary key (id))
create table el_survey_question (id bigint identity not null, insert_user_id varchar(30), insert_dt datetime2, ex1 varchar(500), ex1_score int, ex2 varchar(500), ex2_score int, ex3 varchar(500), ex3_score int, ex4 varchar(500), ex4_score int, ex5 varchar(500), ex5_score int, question varchar(1000), survey_gubun varchar(1), survey_id bigint, primary key (id))
create table job_description (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, short_name varchar(10) not null, title varchar(255), primary key (id))
create table job_description_version (id bigint not null, insert_user_id varchar(30), insert_dt datetime2, update_user_id varchar(30), update_dt datetime2, active bit not null, release_date datetime2, version_no varchar(255), jd_id bigint, job_description_version_file_id int, primary key (id))
create table job_description_version_file (id int identity not null, insert_user_id varchar(30), insert_dt datetime2, file_name varchar(255) not null, mime_type varchar(255), save_name varchar(255) not null, size bigint, job_description_version_id bigint, primary key (id))
create table privilege (id bigint not null, name varchar(255), primary key (id))
create table role (id bigint not null, memo varchar(255), name varchar(255), primary key (id))
create table roles_privileges (role_id bigint not null, privilege_id bigint not null)
alter table job_description add constraint UKngfovmg54i475m02u0adw69la unique (short_name)
alter table account_roles add constraint FKi84870gssnbi37wfqfifekghb foreign key (role_id) references role
alter table account_roles add constraint FKg75jws9c251epgg4r5swxqkn3 foreign key (user_id) references account
alter table binder_cv add constraint FKt45e0m4jqbvm7uclr8rlldrus foreign key (user_id) references account
alter table binder_cv_career_history add constraint FKpft9unqimf38fpoqt4xev5aoj foreign key (cv_id) references binder_cv
alter table binder_cv_education add constraint FK3q8qswqdgorw9fauc8cgfxwxu foreign key (cv_id) references binder_cv
alter table binder_cv_experience add constraint FKixktpnsa2agyifc9sc6rf6ylq foreign key (cv_id) references binder_cv
alter table binder_cv_licenses_certification add constraint FKbarbt46jlhmbd1byh8ip6ueil foreign key (cv_id) references binder_cv
alter table binder_cv_professional_affiliations add constraint FKi6aryri7vfppffhmye4ehmwan foreign key (cv_id) references binder_cv
alter table binder_cv_skills add constraint FKp7mb8s7i594ci8h66ictexyjj foreign key (cv_id) references binder_cv
alter table el_border add constraint FK21s2onht2fhjp8uyojj4mxwm5 foreign key (type_id) references el_border_master
alter table el_border_file add constraint FKcw89wetecuprrf6v902daj6lg foreign key (border_id) references el_border
alter table el_course add constraint FK60syffjj0r1lwvgsb3ecwt7xp foreign key (type_id) references el_course_master
alter table el_course_account add constraint FKq3pu8y0k2sbqcyqaqsrwdrfvg foreign key (user_id) references account
alter table el_course_account add constraint FK55rtxqqxyj2rylvopljr4wgxa foreign key (course_id) references el_course
alter table el_course_account add constraint FK23ejjw8rtpv6ycsoft35ia7fc foreign key (appr_user_id1) references account
alter table el_course_account add constraint FK3jhgcihcqlkj2qybgukcp3q1d foreign key (appr_user_id2) references account
alter table el_course_file add constraint FK10w3tpfw9xgwwtcgac0yyai6r foreign key (course_id) references el_course
alter table el_course_quiz add constraint FK68gu1w947wfxms915lcbyardo foreign key (course_id) references el_course
alter table el_course_quiz_action add constraint FK3v4uuqb57n2d2ln1vvqti33xc foreign key (user_id) references account
alter table el_course_quiz_action add constraint FKkr7akh2wbeu4lpn7t5m1ty648 foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_action_answer add constraint FKdsvw10xikq1h7c3ae4hxf7j95 foreign key (question_id) references el_course_quiz_question
alter table el_course_quiz_action_answer add constraint FK5v87kssshbcwlxgc88i9sc8nr foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_action_history add constraint FKqtb08y0lr8rd89h4dcl1qs4n6 foreign key (quiz_action_id) references el_course_quiz_action
alter table el_course_quiz_file add constraint FK9sl5f3tha9vawrgkddig09d4f foreign key (quiz_id) references el_course_quiz
alter table el_course_quiz_question add constraint FK9siuo8yywgwi0vyg0fibgv203 foreign key (quiz_id) references el_course_quiz
alter table el_course_section add constraint FKq5b0qs3n2b8alit9u9ocvgobl foreign key (course_id) references el_course
alter table el_course_section add constraint FK1gjwgd0ap6qie4we9niei56oq foreign key (type) references el_minor
alter table el_course_section_action add constraint FKf0s43o6k2xgaf1ng8p4wl64xr foreign key (user_id) references account
alter table el_course_section_action add constraint FKnkv1oju5ob41ck6x4c9515qba foreign key (section_id) references el_course_section
alter table el_course_section_action_history add constraint FK3d1agwcbs7ayhnw4eb9e0tfh4 foreign key (section_action_id) references el_course_section_action
alter table el_course_section_file add constraint FKpr7phqcmgx1vw4tpll4ggxx5v foreign key (section_id) references el_course_section
alter table el_course_survey add constraint FKevblhe42cdduo8s8sdwh4wpyy foreign key (course_id) references el_course
alter table el_course_survey add constraint FKgd6wk31ndjgq6ydk7hsi86abq foreign key (survey_id) references el_survey
alter table el_course_survey_action add constraint FKj3y4acv9luwbwy5bfsckevhw1 foreign key (user_id) references account
alter table el_course_survey_action add constraint FK62hfx9i7bqh44sglkqp7p1y9u foreign key (course_survey_id) references el_course_survey
alter table el_course_survey_action_answer add constraint FK6rk6wgjkwsp2rd3i3l5lp8h7j foreign key (question_id) references el_course_survey_question
alter table el_course_survey_action_answer add constraint FKgyxvrtewve15vtgx25aw87wxa foreign key (survey_action_id) references el_course_survey_action
alter table el_course_survey_question add constraint FK51xwu5kxsnro0ugx14vs71imi foreign key (course_survey_id) references el_course_survey
alter table el_document add constraint FKm8eli3gaag5yhulrsi0podxcl foreign key (user_id) references account
alter table el_document add constraint FKhfbwa32sjnl86lpw7d6dtwb5r foreign key (template_id) references el_document_template
alter table el_document_account add constraint FKsrdpctfwv15l9ep8s04k1p0e1 foreign key (user_id) references account
alter table el_document_account add constraint FKpb3m5b0sbmp7mb2jkfetpmfuw foreign key (document_id) references el_document
alter table el_document_account add constraint FKohngh0k7n0haxd4e80kwv3e07 foreign key (appr_user_id1) references account
alter table el_document_account add constraint FKft9o4p9wtorwl74n80tl1gw2i foreign key (appr_user_id2) references account
alter table el_document_course_account add constraint FKs9f5rq3no8wyelk7klf9gu4g6 foreign key (user_id) references account
alter table el_document_course_account add constraint FK2ydaw5fo5hj538sqjy9q2raj1 foreign key (document_id) references el_document
alter table el_document_file add constraint FK3295kci06c28yw1ggm8o8xcs foreign key (doc_id) references el_document
alter table el_minor add constraint major_cd foreign key (major_cd) references el_major
alter table el_schedule_file add constraint FK9dhfneksmem35xxth48gj7ayi foreign key (schedule_id) references el_schedule
alter table el_survey_file add constraint FK7uwsaha8n6o4q7r3bcfol56f0 foreign key (survey_id) references el_survey
alter table el_survey_question add constraint FKcf31kt61himjwlb682yh8j108 foreign key (survey_id) references el_survey
alter table job_description_version add constraint FKiislbdjiimp3bawl481264a38 foreign key (jd_id) references job_description
alter table job_description_version add constraint FKp7uunxfqe7ep48xwptgaoh7gq foreign key (job_description_version_file_id) references job_description_version_file
alter table job_description_version_file add constraint FKjc9yctk3do6s713vfy1hd2jme foreign key (job_description_version_id) references job_description_version
alter table roles_privileges add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege
alter table roles_privileges add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role
