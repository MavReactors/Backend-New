package Mavreactors.app.Controller;

import Mavreactors.app.Mapper.OutfitMapper;
import Mavreactors.app.Model.Outfit;
import Mavreactors.app.Model.Session;
import Mavreactors.app.Model.User;
import Mavreactors.app.Model.Vote;
import Mavreactors.app.Repository.SessionRepository;
import Mavreactors.app.Repository.UserRepository;
import Mavreactors.app.Service.OutfitService;
import Mavreactors.app.Service.VoteService;
import Mavreactors.app.dto.OutfitDto;
import Mavreactors.app.dto.PrendasDto;
import Mavreactors.app.dto.VoteDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
@RestController
@RequestMapping("/api/customer")
public class VoteController {

    private final OutfitService outfitService;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<VoteDto> createVote(@RequestBody VoteDto voteDto, @CookieValue("authToken") UUID authToken){
        Session session = sessionRepository.findByToken(authToken);
        User user = session.getUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Devuelve un error si el usuario no se encuentra
        }
        voteDto.setUserEmail(user.getEmail());
        voteDto.setUser(user);
        UUID token = UUID.randomUUID();
        voteDto.setVoteId(token);
        Outfit outfit = OutfitMapper.mapToOutfit(outfitService.getOutfitById(voteDto.getOutfitId()));
        voteDto.setOutfit(outfit);
        VoteDto saveVote = voteService.createVote(voteDto);
        return new ResponseEntity<>(saveVote, HttpStatus.CREATED);
    }

    @GetMapping("/vote")
    public ResponseEntity<List<VoteDto>> getAllVotesByOutfits(){
        List<VoteDto> votes = voteService.getAllVotesByOutfits();
        return ResponseEntity.ok(votes);
    }
}
