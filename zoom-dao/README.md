# 约定


# 重命名规则

#### 单表
检测字段前缀，通过驼峰式并省略前缀的方式来命名。
检测前缀的规则为： 
如果字段名称为驼峰式，则改成_式，如果全大写，则改成全小写
将字段以_ 分割成数组，比较第一个元素，取出现次数最多的为前缀
改名规则： 
有前缀，则去掉前缀，
去掉_并将_紧跟着的第一个字符大写
如表:sys_user的字段为
USR_ID,USR_NAME,USR_ACCOUNT,USR_PASSWORD,GROUP_ID
对应: id,name,account,password,groupId

如表:sys_user的字段为:
usrId,USR_NAME,usrAccount,Usr_Password,groupId  (如果_紧跟着大写字母，形式为 _usr__password)，前面的_去掉省掉中间的__
对应:id,name,account,password,groupId,
当然这里的命名很奇葩，现实的命名规则需要通过约定来约束。

#### 多表

规则： 映射 table.column=>最短能区分的字段,
比如: 表
sys_user:USR_ID,USR_NAME,USR_ACCOUNT,USR_PASSWORD,GP_ID
sys_group:GP_ID,GP_NAME
sys_user_method: UM_ID,UM_NAME
两张表
最短字段为
id,name,account,password,gpId,gpName
这里有个问题，如何确定两张表同一个名称的字段值是同一个？这里确定不了，所以这个方案不太可行
id,name,account,password,gpId,groupId,groupName
两张表表名称按同样规则取出user,group,第一张表不需要前缀，第二张表开始需要加前缀
这样对应查询:
select(id,name,account...)
编译成sql:
select sys_user.USR_ID as F0 ... where sys_user.USR_ID=? order by xx group by xxx 都需要加上表前缀
最后取值: F0=>id
如果有count\max\min\sum\average的情况,这个是手动加的，到时候手动加上映射关系.
select .... count(*) as F100,SUM(SYS_USER.USR_SCORE) AS F200 .....
编译的时候： 找到map里面的key对应的实际查询字段，查询完成之后，找到查询字段对应的值的key

Entity
getSelectField()

ar.rawMode(true),跳过重命名过程，直接是原始的名称，全手动操作。
ar.ignoreNull(false),支持null入库

dao.clear() 清除缓存


Entity 对应Map
键 : table/select xxx,如果select里面没有函数，那么直接用，有函数，需要使用函数确认，所以是两个entity
比如: select count(*) a from a

entity : a里面的拿出来 , select 部分 count(*)

select count(*) as c from a 
流程:
sql为 select count(*) as f0 from a
保存:  ['a']
结果及: ResultSet rs   result.put('a', rs.get(0))

//原始字段 重命名  映射字段
// 取值 : 原始字段=>值
select a.c1 as f0 ,a.c2 as f2 from a
保存 ['c1','c2']   result.put('c1',rs.get(0));







