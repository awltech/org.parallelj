select aa.job_group, aa.job_name, aa.uid, aa.restarted_uid, aa.return_code, aa.time_end,
    coalesce(s.success,0) as success, 
    coalesce(aa.failure,0) as failure, 
    coalesce(s.success,0)+coalesce(aa.failure,0) as total from
(
    SELECT a.job_group, a.job_name, a.uid, a.restarted_uid, a.return_code, a.time_end, f.failure FROM qrtz_track_job_details as a
    left join
    (SELECT uid,count(*) as failure FROM qrtz_track_iterations where success='0' group by uid) as f
    on a.uid = f.uid
) aa
left join
(SELECT uid,count(*) as success FROM qrtz_track_iterations where success='1' group by uid) as s
on aa.uid = s.uid
order by aa.job_group asc, aa.job_name asc, aa.uid asc