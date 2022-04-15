package com.example.challenge.service;

import com.example.challenge.dto.ChallengeDto;
import com.example.challenge.model.Challenge;
import com.example.challenge.model.UserChallenge;
import com.example.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService{

    private final ChallengeRepository challengeRepository;


    @Transactional
    @Override //챌린지 추가(관리자)
    public void addChallenge(ChallengeDto challengeDto) {
        log.info("add challenge");
        int totalCount = 0;
        if(challengeDto.getPeriod() % 7 >= challengeDto.getWeekCount()) {
            totalCount = (int)Math.floor(challengeDto.getPeriod() / 7) * challengeDto.getWeekCount() + challengeDto.getWeekCount();
        } else {
            totalCount = (int)Math.floor(challengeDto.getPeriod() / 7) * challengeDto.getWeekCount();
        }
//        log.info("totalCount : {}", totalCount);
        challengeRepository.save(Challenge.builder()
                .id(null)
                .challengeTitle(challengeDto.getChallengeTitle())
                .challengeDesc(challengeDto.getChallengeDesc())
                .challengeImg(challengeDto.getChallengeImg())
                .startDay(challengeDto.getStartDay())
                .endDay(challengeDto.getEndDay())
                .period(challengeDto.getPeriod())
                .weekCount(challengeDto.getWeekCount())
                .totalCount(totalCount)
                .challengeState(challengeDto.getChallengeState())
                .build());
    }

    @Transactional
    @Override //챌린지 수정(관리자)
    public void editChallenge(Long id, ChallengeDto challengeDto) {
//        log.info("edit challenge. {}", challengeRepository.findById(challengeDto.getChallengeListId()).get());
        if (challengeRepository.findById(id).isPresent()) {
            Challenge editedChallenge = Challenge
                    .builder()
                    .id(id)
                    .challengeTitle(challengeDto.getChallengeTitle())
                    .challengeDesc(challengeDto.getChallengeDesc())
                    .challengeImg(challengeDto.getChallengeImg())
                    .startDay(challengeDto.getStartDay())
                    .endDay(challengeDto.getEndDay())
                    .period(challengeDto.getPeriod())
                    .weekCount(challengeDto.getWeekCount())
                    .totalCount(challengeDto.getTotalCount())
                    .challengeState(challengeDto.getChallengeState())
                    .build();
            challengeRepository.save(editedChallenge);
        }else {
            log.error("edit challenge error.");
        }
    }

    @Transactional
    @Override //getAll 챌린지
    public List<Challenge> getAllChallenge() {
        log.info("get all challenge");
        return challengeRepository.findAll();
    }

    @Transactional
    @Override //getById 챌린지
    public Optional<Challenge> getChallengeById(Long id) {
        log.info("get challenge by challenge id {}.", id);
        return Optional.ofNullable(challengeRepository.findById(id).get());
    }

    @Transactional
    @Override //챌린지 삭제
    public void delChallenge(Long id) {
        log.info("delete challenge by id {}.", id);
        challengeRepository.deleteById(id);
    }



    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 0시에 실행
    public void scheduler(){
        List<Challenge> list = challengeRepository.findAll();
        LocalDate today = LocalDate.now();

        list.forEach(challenge -> {
            if (challenge.getEndDay().isBefore(today) && challenge.getChallengeState() == 0){
                challenge.setChallengeState(1); //endDay 확인하고 state 변경(종료되면 1)
            }
        });
    }
}
