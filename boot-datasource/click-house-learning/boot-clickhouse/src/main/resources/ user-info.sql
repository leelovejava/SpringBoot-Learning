CREATE TABLE cs_user_info (
  `id` UInt64,
  `user_name` String,
  `pass_word` String,
  `phone` String,
  `email` String,
  `create_day` Date DEFAULT CAST(now(),'Date')
) ENGINE = MergeTree(create_day, intHash32(id), 8192)

INSERT INTO cs_user_info
  (id,user_name,pass_word,phone,email)
VALUES
  (1,'cicada','123','13923456789','cicada@com'),
  (2,'smile','234','13922226789','smile@com'),
  (3,'spring','345','13966666789','spring@com');

SELECT * FROM cs_user_info ;
SELECT * FROM cs_user_info WHERE user_name='smile' AND pass_word='234';
SELECT * FROM cs_user_info WHERE id IN (1,2);
SELECT * FROM cs_user_info WHERE id=1 OR id=2 OR id=3;