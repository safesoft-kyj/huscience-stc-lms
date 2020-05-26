-- 교육완료보고서 알림 발송시 주말 일수 구하는 함수
CREATE FUNCTION [dbo].[fn_GetTotalHolidays]
(
    @startdate Date,
    @enddate Date
)
RETURNS INT
AS
BEGIN
	DECLARE @TotHolidayDays INT;
	DECLARE @tempDate Date;

	set @TotHolidayDays = 0;

	while @startdate <= @enddate
	begin

		if(DATEPART(dw, @enddate) in (1, 7))
		begin
			set @TotHolidayDays = @TotHolidayDays + 1
		end

		set @enddate = dateadd(day, -1, @enddate)
	end
	if (@TotHolidayDays % 2 = 1) set @TotHolidayDays = @TotHolidayDays + 1;

	return @TotHolidayDays
end

