package YAMSABU.BreatheLion_backend.domain.organization.config;

import YAMSABU.BreatheLion_backend.domain.organization.entity.Organization;
import YAMSABU.BreatheLion_backend.domain.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrgConfig {

    @Bean
    @Transactional
    CommandLineRunner seedOrganizations(OrganizationRepository repo) {
        return args -> {

            record OrgSeed(String name, String phone, String url, String desc) {}
            List<OrgSeed> items = List.of(
                    new OrgSeed("중앙디지털성범죄피해자지원센터","02-735-8994","https://d4u.stop.or.kr/","중앙디지털성범죄피해자지원센터는 디지털 성범죄 피해에 대한 접수 등 상담, 삭제지원 및 유포 현황 모니터링, 수사·법률·의료 연계 지원을 제공하는 기관입니다."),
                    new OrgSeed("110 정부민원안내콜센터","110","https://www.110.go.kr/start.do","정부민원안내콜센터(110)는 정부민원에 대한 전화상담, 카카오톡 상담, 온라인(화상수어, 채팅, SNS) 상담을 제공하는 기관입니다. (갑질상담 가능)"),
                    new OrgSeed("금융감독원콜센터","1332","https://www.fss.or.kr/fss/s1332/s1332Index/sub.do?menuNo=200037","불법사금융 · 개인정보 불법유통 피해신고 접수 및 수사기관 통보, 금융법률지원 연계 상담을 제공하는 기관입니다."),
                    new OrgSeed("보건복지상담센터","129","https://www.129.go.kr/","보건복지 관련 정보와 상담 서비스를 신속 정확하게 제공하는 기관입니다."),
                    new OrgSeed("고용노동부 고객상담센터","1350","http://1350.moel.go.kr/home/","누구든지 직장 내 괴롭힘 발생 사실을 알게 된 경우에는 그 사실을 사용자에게 신고할 수 있으며, 고용노동부 고객상담센터 기관입니다."),
                    new OrgSeed("117학교폭력 신고센터","117","https://www.safe182.go.kr/cont/homeContents.do?contentsNm=intro_portal117","117학교폭력 신고센터는 학교·여성폭력 및 성매매피해자 긴급지원센터입니다."),
                    new OrgSeed("청소년 1388","1388","https://www.1388.go.kr/ind/YTOSP_SC_IND_01","상담 및 복지지원이 필요한 청소년을 발굴하는 상담채널로서 365일 24시간 온라인상담 서비스를 제공하는 기관입니다."),
                    new OrgSeed("한국성폭력상담소","02-338-5801","https://www.sisters.or.kr/","한국성폭력상담소는 차별과 혐오를 확대하는 사회문화에 맞서 평등하게 관계 맺고 나다운 모습으로 함께 사는 세상을 위해 활동하는 기관입니다."),
                    new OrgSeed("여성긴급전화","1366","https://women1366.kr/?menuno=222","여성 폭력으로 긴급한 구조·보호 또는 상담을 위한 서비스 센터입니다."),
                    new OrgSeed("시민참여복지회 경기가정폭력상담소","031-419-1366","http://cpwa.or.kr/","시민참여복지회 경기가정폭력상담소는 정부가 지원하여 무료로 상담하는 기관입니다. (성폭력, 성매매 포함)"),
                    new OrgSeed("경찰서","112","https://www.police.go.kr/user/bbs/BD_selectBbsList.do?q_bbsCode=1038&q_tab=1","경찰서는 긴급 상황 시 즉시 연결되는 대표 번호 112를 제공하는 기관입니다.")
            );

            int inserted = 0;
            for (OrgSeed it : items) {
                boolean exists = repo.existsByName(it.name());
                if (!exists) {
                    repo.save(Organization.builder()
                            .name(it.name())
                            .phone(it.phone())
                            .url(it.url())
                            .description(it.desc())
                            .build());
                    inserted++;
                }
            }
            log.info("✅ Organization seeding done. inserted={}, total={}", inserted, repo.count());
        };
    }
}