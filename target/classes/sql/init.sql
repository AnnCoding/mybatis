CREATE TABLE `tb_user` (
`id` char(32) NOT NULL,
`user_name` varchar(32) DEFAULT NULL,
`password` varchar(32) DEFAULT NULL,
`name` varchar(32) DEFAULT NULL,
`age` int(10) DEFAULT NULL,
`sex` int(4) DEFAULT NULL,
`birthday` date DEFAULT NULL,
`created` datetime DEFAULT NULL,
`updated` datetime DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

