getListMd1
===
select * from address WHERE  `address_id` = #{id}

getListMd2
===
select * from address WHERE  `address_id` = #{id}


updateMd1
===
UPDATE
`address`
SET
`name` = #{name}
WHERE  `address_id` = #{id}


updateMd2
===
UPDATE
`address`
SET
`name` = #{name}
WHERE  `address_id` = #{id}
