CREATE TABLE [dbo].[el_major](
	[major_cd] [varchar](5) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[update_user_id] [varchar](30) NULL,
	[update_dt] [datetime2](7) NULL,
	[major_nm] [varchar](30) NOT NULL,
	[type] [varchar](1) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[major_cd] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA01', N'pub147', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'pub147', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'게시판 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA02', N'pub147', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'pub147', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'사용자 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA03', N'pub147', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'pub147', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'사용 유무', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC01', N'pub147', CAST(N'2019-09-28T19:06:47.1240000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:42:50.2510000' AS DateTime2), N'교육과정', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC02', N'pub147', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'pub147', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'문제 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC03', N'pub147', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'pub147', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'강의구분', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC04', N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'교육신청일자유형', N'S')
GO
CREATE TABLE [dbo].[el_minor](
	[minor_cd] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[update_user_id] [varchar](30) NULL,
	[update_dt] [datetime2](7) NULL,
	[minor_nm] [varchar](30) NOT NULL,
	[seq] [int] NOT NULL,
	[major_cd] [varchar](5) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[minor_cd] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'0', N'pub147', CAST(N'2019-10-07T19:08:08.1760000' AS DateTime2), N'pub147', CAST(N'2019-10-07T19:08:08.1760000' AS DateTime2), N'No', 2, N'BA03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'1', N'pub147', CAST(N'2019-10-07T19:08:21.5070000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:51:42.2080000' AS DateTime2), N'상시', 1, N'BC04')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'2', N'admin', CAST(N'2019-10-11T10:51:53.0050000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:51:53.0050000' AS DateTime2), N'기간', 2, N'BC04')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0101', N'pub147', CAST(N'2019-09-28T19:07:18.7850000' AS DateTime2), N'pub147', CAST(N'2019-09-28T19:07:18.7850000' AS DateTime2), N'공지게시판', 1, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0102', N'pub147', CAST(N'2019-09-28T19:07:30.8850000' AS DateTime2), N'pub147', CAST(N'2019-09-28T19:10:04.4400000' AS DateTime2), N'일반게시판', 2, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0103', N'pub147', CAST(N'2019-09-28T19:11:06.2650000' AS DateTime2), N'pub147', CAST(N'2019-09-28T19:11:06.2650000' AS DateTime2), N'자료실', 3, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0101', N'pub147', CAST(N'2019-09-28T19:08:15.8590000' AS DateTime2), N'admin', CAST(N'2019-10-11T11:09:05.4860000' AS DateTime2), N'온라인', 1, N'BC01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0102', N'pub147', CAST(N'2019-09-28T19:11:33.8820000' AS DateTime2), N'admin', CAST(N'2019-10-11T11:09:12.2870000' AS DateTime2), N'오프라인', 2, N'BC01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0201', N'pub147', CAST(N'2019-10-01T17:50:11.5380000' AS DateTime2), N'pub147', CAST(N'2019-10-01T17:50:11.5380000' AS DateTime2), N'객관식', 1, N'BC02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0202', N'pub147', CAST(N'2019-10-01T17:50:31.2880000' AS DateTime2), N'pub147', CAST(N'2019-10-01T17:50:31.2880000' AS DateTime2), N'주관식', 2, N'BC02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0301', N'pub147', CAST(N'2019-10-02T00:47:27.1040000' AS DateTime2), N'pub147', CAST(N'2019-10-02T00:47:27.1040000' AS DateTime2), N'강의', 1, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0302', N'pub147', CAST(N'2019-10-02T12:46:30.1640000' AS DateTime2), N'pub147', CAST(N'2019-10-23T12:58:49.8460000' AS DateTime2), N'시험', 2, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0303', N'pub147', CAST(N'2019-10-02T12:46:50.9950000' AS DateTime2), N'pub147', CAST(N'2019-10-02T12:46:50.9950000' AS DateTime2), N'설문', 3, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'O', N'pub147', CAST(N'2019-10-07T17:06:28.7990000' AS DateTime2), N'pub147', CAST(N'2019-10-07T17:06:28.7990000' AS DateTime2), N'외부사용자', 3, N'BA02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'S', N'pub147', CAST(N'2019-10-17T21:25:08.8670000' AS DateTime2), N'pub147', CAST(N'2019-10-17T21:25:08.8670000' AS DateTime2), N'시스템 관리자', 3, N'BA02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'U', N'pub147', CAST(N'2019-10-07T17:06:17.1400000' AS DateTime2), N'pub147', CAST(N'2019-10-17T21:22:18.0040000' AS DateTime2), N'내부직원', 2, N'BA02')
ALTER TABLE [dbo].[el_minor] ADD  DEFAULT ((1)) FOR [seq]
GO
ALTER TABLE [dbo].[el_minor]  WITH CHECK ADD  CONSTRAINT [major_cd] FOREIGN KEY([major_cd])
REFERENCES [dbo].[el_major] ([major_cd])
GO
ALTER TABLE [dbo].[el_minor] CHECK CONSTRAINT [major_cd]
GO
CREATE TABLE [dbo].[el_course_master](
	[id] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[course_name] [varchar](100) NOT NULL,
	[minor_cd] [varchar](10) NOT NULL,
	[request_type] [varchar](1) NOT NULL,
	[is_course_manger_approval] [varchar](255) NULL,
	[is_team_manger_approval] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [minor_cd], [request_type], [is_course_manger_approval], [is_team_manger_approval]) VALUES (N'BC0101', N'pub147', CAST(N'2019-10-13T16:29:50.1190000' AS DateTime2), N'self-training', N'BC0101', N'1', N'N', N'N')
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [minor_cd], [request_type], [is_course_manger_approval], [is_team_manger_approval]) VALUES (N'BC0102', N'pub147', CAST(N'2019-10-13T16:29:59.2190000' AS DateTime2), N'class training', N'BC0102', N'2', N'Y', N'Y')
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [minor_cd], [request_type], [is_course_manger_approval], [is_team_manger_approval]) VALUES (N'BC0103', N'pub147', CAST(N'2019-10-13T16:30:08.9090000' AS DateTime2), N'부서별 교육', N'BC0102', N'2', N'N', N'Y')
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [minor_cd], [request_type], [is_course_manger_approval], [is_team_manger_approval]) VALUES (N'BC0104', N'pub147', CAST(N'2019-10-13T16:30:18.6840000' AS DateTime2), N'외부 교육', N'BC0101', N'2', N'N', N'Y')
ALTER TABLE [dbo].[el_course_master] ADD  DEFAULT ((1)) FOR [request_type]
GO
CREATE TABLE [dbo].[el_border_master](
	[id] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[border_name] [varchar](100) NOT NULL,
	[is_mail] [varchar](1) NOT NULL,
	[minor_cd] [varchar](10) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0101', N'admin', CAST(N'2019-10-11T10:38:55.4760000' AS DateTime2), N'공지사항', N'N', N'BA0101')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0102', N'admin', CAST(N'2019-10-11T10:39:16.6510000' AS DateTime2), N'법규/가이드라인', N'N', N'BA0102')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0103', N'admin', CAST(N'2019-10-11T10:39:33.8600000' AS DateTime2), N'자료실', N'N', N'BA0103')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0104', N'pub147', CAST(N'2019-10-16T15:54:13.4920000' AS DateTime2), N'What''s New', N'N', N'BA0102')
GO
CREATE TABLE [dbo].[el_survey](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[el_survey] ON

INSERT [dbo].[el_survey] ([id], [insert_user_id], [insert_dt], [name]) VALUES (1, N'pub147', CAST(N'2019-11-11T23:59:25.3930000' AS DateTime2), N'테스트 설문')
SET IDENTITY_INSERT [dbo].[el_survey] OFF

GO
CREATE TABLE [dbo].[el_survey_file](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[file_name] [varchar](255) NULL,
	[mime_type] [varchar](255) NULL,
	[save_name] [varchar](255) NULL,
	[size] [bigint] NULL,
	[survey_id] [bigint] NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[el_survey_file] ON

INSERT [dbo].[el_survey_file] ([id], [insert_user_id], [insert_dt], [file_name], [mime_type], [save_name], [size], [survey_id]) VALUES (1, N'pub147', CAST(N'2019-11-11T23:59:25.4230000' AS DateTime2), N'설문sample_eng.xlsx', N'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', N'1d405b8c-e9de-4c9b-a99b-81a6dd1261bb_설문sample_eng.xlsx', 10363, 1)
SET IDENTITY_INSERT [dbo].[el_survey_file] OFF
ALTER TABLE [dbo].[el_survey_file]  WITH CHECK ADD  CONSTRAINT [FK7uwsaha8n6o4q7r3bcfol56f0] FOREIGN KEY([survey_id])
REFERENCES [dbo].[el_survey] ([id])
GO
ALTER TABLE [dbo].[el_survey_file] CHECK CONSTRAINT [FK7uwsaha8n6o4q7r3bcfol56f0]
CREATE TABLE [dbo].[el_survey_question](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[ex1] [varchar](500) NULL,
	[ex1_score] [int] NULL,
	[ex2] [varchar](500) NULL,
	[ex2_score] [int] NULL,
	[ex3] [varchar](500) NULL,
	[ex3_score] [int] NULL,
	[ex4] [varchar](500) NULL,
	[ex4_score] [int] NULL,
	[ex5] [varchar](500) NULL,
	[ex5_score] [int] NULL,
	[question] [varchar](1000) NULL,
	[survey_gubun] [varchar](1) NULL,
	[survey_id] [bigint] NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[el_survey_question] ON

INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (1, N'pub147', CAST(N'2019-11-11T23:59:25.7680000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'1-1. Was the training relevant to the topic?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (2, N'pub147', CAST(N'2019-11-11T23:59:25.7760000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'1-2. Was the content organized and easy to follow?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (3, N'pub147', CAST(N'2019-11-11T23:59:25.7780000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'1-3. Was the training useful in your current work?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (4, N'pub147', CAST(N'2019-11-11T23:59:25.7810000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'1-4. Was the time allotted for the training sufficient?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (5, N'pub147', CAST(N'2019-11-11T23:59:25.7830000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'2.1. Was sufficient information given prior to training?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (6, N'pub147', CAST(N'2019-11-11T23:59:25.7860000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'2.2. Were materials distributed adequately?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (7, N'pub147', CAST(N'2019-11-11T23:59:25.7880000' AS DateTime2), N'Very satisfied', 5, N'Satisfied', 4, N'Neutral', 3, N'Dissatisfied', 2, N'Very Dissatisfied', 1, N'2.3. Were the meeting room and facilities satisfactory?', N'M', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (8, N'pub147', CAST(N'2019-11-11T23:59:25.7900000' AS DateTime2), N'', 0, N'', 0, N'', 0, N'', 0, N'', 0, N'If you have rated dissatisfied/very dissatisfied, please state the reason', N'S', 1)
INSERT [dbo].[el_survey_question] ([id], [insert_user_id], [insert_dt], [ex1], [ex1_score], [ex2], [ex2_score], [ex3], [ex3_score], [ex4], [ex4_score], [ex5], [ex5_score], [question], [survey_gubun], [survey_id]) VALUES (9, N'pub147', CAST(N'2019-11-11T23:59:25.7930000' AS DateTime2), N'', 0, N'', 0, N'', 0, N'', 0, N'', 0, N'If you would like other type of training offered, please suggest here.', N'S', 1)
SET IDENTITY_INSERT [dbo].[el_survey_question] OFF
ALTER TABLE [dbo].[el_survey_question]  WITH CHECK ADD  CONSTRAINT [FKcf31kt61himjwlb682yh8j108] FOREIGN KEY([survey_id])
REFERENCES [dbo].[el_survey] ([id])
GO
ALTER TABLE [dbo].[el_survey_question] CHECK CONSTRAINT [FKcf31kt61himjwlb682yh8j108]
GO

