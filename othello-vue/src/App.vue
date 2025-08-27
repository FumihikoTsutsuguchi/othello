<template>
    <main class="container">
        <h1>Othello (Human vs CPU)</h1>
        <section class="controls">
            <button @click="newGame">新しい対局</button>
            <span v-if="gameId">ID: {{ gameId }}</span>
        </section>

        <section v-if="board">
            <p class="status" v-if="!finished">
                手番: <strong>{{ currentPlayer === 1 ? "あなた(黒)" : "CPU(白)" }}</strong>
            </p>
            <p class="status" v-else>
                対局終了：
                <strong>
                    {{ winner === null ? "引き分け" : winner === 1 ? "あなた(黒)の勝ち" : "CPU(白)の勝ち" }}
                </strong>
            </p>
            <Board :board="board" :legal="legal" @play="tryPlay" />
        </section>
    </main>
</template>

<script>
import { ref } from "vue";
import Board from "./components/Board.vue";
import { createGame, play, legalMoves } from "./api.js";

export default {
    components: { Board },
    setup() {
        const gameId = ref(null);
        const board = ref(null);
        const currentPlayer = ref(1);
        const finished = ref(false);
        const winner = ref(null);
        const legal = ref([]);

        async function refreshLegal() {
            if (!gameId.value || finished.value || currentPlayer.value !== 1) {
                legal.value = [];
                return;
            }
            legal.value = await legalMoves(gameId.value, 1);
        }

        async function newGame() {
            const g = await createGame();
            gameId.value = g.gameId;
            board.value = g.board;
            currentPlayer.value = g.currentPlayer;
            finished.value = g.finished;
            winner.value = g.winner;
            await refreshLegal();
        }

        async function tryPlay(x, y) {
            if (!gameId.value || !board.value || finished.value || currentPlayer.value !== 1) return;
            try {
                const g = await play(gameId.value, x, y);
                board.value = g.board;
                currentPlayer.value = g.currentPlayer;
                finished.value = g.finished;
                winner.value = g.winner;
                await refreshLegal();
            } catch (e) {
                console.error(e);
            }
        }

        return { gameId, board, currentPlayer, finished, winner, legal, newGame, tryPlay };
    },
};
</script>

<style scoped>
.container {
    max-width: 720px;
    margin: 24px auto;
    padding: 0 12px;
    font-family: system-ui, -apple-system, Segoe UI, Roboto, "Helvetica Neue", Arial;
}
h1 {
    font-size: 24px;
    margin-bottom: 12px;
}
.controls {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}
button {
    padding: 8px 12px;
    border-radius: 8px;
    border: 1px solid #ccc;
    background: #fff;
    cursor: pointer;
}
.status {
    margin: 8px 0 12px;
}
</style>
