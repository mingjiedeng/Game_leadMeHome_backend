package com.ming.game.gameData;

import com.ming.game.gamedata.GameData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameDataControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongo;

    @Before
    public void setup() {
        GameData gameData = new GameData(1);
        mongo.dropCollection("gameData");
        mongo.insert(gameData, "gameData");
    }

    @Test
    public void whenRequestGameData_thenRespondGameData() throws Exception {
        mockMvc.perform(get("/gameData/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("level")));
    }

    @Test
    public void whenRequestLevelAmount_thenReturnLevelAmount() throws Exception {
        mockMvc.perform(get("/gameData/levelsAmount")).andDo(print())
                .andExpect(content().string("1"));
    }
}

