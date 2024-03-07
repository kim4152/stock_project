package com.project.stockproject.stockInform

import com.project.stockproject.room.ItemTable


data class StockInformItem(
    val output: StockOutput,
    val rt_cd: String,
    val msg_cd: String,
    val msg1: String
)

data class StockOutput(
    val iscd_stat_cls_code: String, //00:그외 ,51:관리종목, 52:투자위험, 53:투자경고 ,54:투자주의, 55:신용가능, 57:증거금 100% ,58:거래정지,59:단기과열
    val rprs_mrkt_kor_name:String, //코스닥 or 코스피
    val new_hgpr_lwpr_cls_code: String,    //신고가저가 구분코드(달성했을떄만 조회됨)
    val bstp_kor_isnm: String,            //업종 한글 종목명
    val temp_stop_yn: String,            //임시 정지 여부
    val stck_prpr: String,                //주식 현재가
    val prdy_vrss: String,                //전일 대비
    val prdy_vrss_sign: String,            //전일 대비 부호 1상한 2상승 3보합 4하한 5하락
    val prdy_ctrt: String,                //전일 대비율
    val acml_tr_pbmn: String,            //누적 거래대금
    val acml_vol: String,                //누적 거래량
    val prdy_vrss_vol_rate: String,        //전일 대비 거래량 비율
    val stck_oprc: String,                //주식 시가
    val stck_hgpr: String,            //주식 최고가
    val stck_lwpr: String,                //주식 최저가
    val stck_mxpr: String,            //주식 상한가
    val stck_llam: String,                //주식 하한가
    val stck_sdpr: String,                //주식 기준가
    val hts_frgn_ehrt: String,            //HTS 외국인 소진율
    val frgn_ntby_qty: String,            //외국인 순매수량
    val pgtr_ntby_qty: String,            //프로그램매매 순매수량
    val lstn_stcn: String,                //상장 주수
    val hts_avls: String,                //HTS시가총액
    val per: String,                    //PER
    val pbr: String,                    //PBR
    val vol_tnrt: String,                //거래량 회전율
    val eps: String,                    //EPS
    val bps: String,                //BPS
    val w52_hgpr: String,            //52주 최고가
    val w52_lwpr: String,                //52주 최저가
    val frgn_hldn_qty: String,            //외국인 보유 수량
    val stck_shrn_iscd :String,         //종목코드
    val vi_cls_code: String,            //VI적용구분코드
    val invt_caful_yn: String,            //투자유의 여부 Y/N
    val mrkt_warn_cls_code: String,        //시장경고코드 00없음 01투자주의 02투자경고 03투자위험
    val short_over_yn: String,            //단기과열여부 Y/N
    val sltr_yn: String                //정리매매여부 Y/N
)

data class StockList(
    val stockCode:String,
    val stockName:String,
)


