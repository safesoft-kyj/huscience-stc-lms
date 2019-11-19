
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA01', N'pub147', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'pub147', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'게시판 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA02', N'pub147', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'pub147', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'사용자 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA03', N'pub147', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'pub147', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'사용 유무', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC01', N'pub147', CAST(N'2019-09-28T19:06:47.1240000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:42:50.2510000' AS DateTime2), N'교육과정', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC02', N'pub147', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'pub147', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'문제 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC03', N'pub147', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'pub147', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'강의구분', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC04', N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'교육신청일자유형', N'S')


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

