 
  select * from craw_config;
  select * from patten_code_list;
 select * from page_info_list;
  select count(*)  from page_info_list where updateDt > adddate(sysdate(),-1) ;
  select *  from page_info_list  where failCnt = 0 order by goodsNo ;
  select *  from page_info_list_hist ;
  select*  from page_info_list   where  siteConfigSeq=100031 ;
  select siteConfigSeq,count(*) from page_info_list   where  failCnt = 0  group by siteConfigSeq ;
  

--   delete from page_info_list  ;
--   delete from page_info_list_hist;
--   delete from page_info_list where failCnt = 3 ;

   update page_info_list set updateDt  = adddate(sysdate(),-2)
   where siteConfigSeq = 100031
   and failCnt  = 0
 and pageInfoListSeq =  354838
     ;
    -- 11st
    CALL  insertPageInfoList(100027
    , 'http://m.11st.co.kr/MW/Product/productBasicInfo.tmall?prdNo='
    , 1091703355, 1091703355+5000, adddate(sysdate(),-2));

   -- A
    CALL  insertPageInfoList(100028
    , 'http://mobile.auction.co.kr/Item/ViewItem.aspx?Itemno=A'
    , 889494993, 889494993+5000, adddate(sysdate(),-2));

 -- G
    CALL  insertPageInfoList(100031
    , 'http://mitem.gmarket.co.kr/Item?goodscode='
    , 578633903, 578633903+5000, adddate(sysdate(),-2));
    