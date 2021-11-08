package com.dnd.game.components;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.dnd.game.DndGame;
import com.dnd.game.states.CombatState;
import com.dnd.game.states.DungeonState;
import com.dnd.game.states.GameState;


import java.util.Stack;

public class GameStateManager {

    private DndGame app;
    private Stack<GameState> states;

    public enum State{
        TITLE,
        COMBAT,
        DUNGEON
    }

    public GameStateManager(DndGame app) {
        this.app = app;
        this.states = new Stack<GameState>();
        this.setState(State.DUNGEON);
    }

    public void setState(State state){
        if (states.size()>= 1){
            states.pop().dispose();
        }
        states.push(getState(state));
    }

    private GameState getState(State state) {
        switch(state) {
            case TITLE: break;
            case COMBAT: return new CombatState(this);
            case DUNGEON: return  new DungeonState(this);

        }
        return null;
    }

    public void update(float delta) {
        states.peek().update(delta);
    }

    public void render(){
        states.get(states.size()-1).render();
    }

    public void resize(int w, int h) {
        states.peek().resize(w, h);
    }

    public void dispose(){
        for (GameState g : states){
            g.dispose();
        }
        states.clear();
    }

    public DndGame getApp() {
        return app;
    }
}
