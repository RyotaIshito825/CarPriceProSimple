INSERT INTO car_price_pro.cars(maker,car_model,grade,classification,first_jc,registration_year,registration_month,vi_jc,vi_year,vi_month,price,price_dpf,total_price,total_price_dpf,mileage)
    VALUES ("トヨタ","アルファード","ZS煌 4WD","NEW","REIWA",6,8,"REIWA",9,8,300,9,320,8,10000);
INSERT INTO car_price_pro.cars(maker,car_model,grade,classification,first_jc,registration_year,registration_month,vi_jc,vi_year,vi_month,price,price_dpf,total_price,total_price_dpf,mileage)
    VALUES ("ダイハツ","タントカスタム","RS 4WD 両側電動パワースライド","NEW","REIWA",5,12,"REIWA",8,12,210,1,225,4,6);
INSERT INTO car_price_pro.cars(maker,car_model,grade,classification,first_jc,registration_year,registration_month,price,price_dpf,total_price,total_price_dpf,mileage)
    VALUES ("ホンダ","Nbox","4WD","USED","HEISEI",31,1,90,0,110,2,56789);

INSERT INTO car_price_pro.employees(email, name, password, department, role, delete_flg, created_at, updated_at, oauth_id)
    VALUES ("loveaomori.27@icloud.com", "石戸涼太", "$2a$10$k9dZ.pMYbICYJjDPfV.a1uwN1c9Rr2SMzlY7Bho2En8xmTdNqiXL.", "本社Web", "ADMIN", 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, "12345");
