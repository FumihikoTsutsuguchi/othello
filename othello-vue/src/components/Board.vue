<template>
    <div class="board">
      <div v-for="(row, y) in board" :key="y" class="row">
        <button
          v-for="(cell, x) in row"
          :key="x"
          class="cell"
          :class="{
            black: cell === 1,
            white: cell === 2,
            hint: isHint(x, y)
          }"
          @click="onClick(x, y)"
        >
          <span v-if="cell === 1">●</span>
          <span v-else-if="cell === 2">○</span>
        </button>
      </div>
    </div>
  </template>

  <script>
  export default {
    props: {
      board: { type: Array, required: true },
      legal: { type: Array, default: () => [] }
    },
    emits: ['play'],
    methods: {
      isHint(x, y) {
        return this.legal.some(p => p.x === x && p.y === y)
      },
      onClick(x, y) {
        this.$emit('play', x, y)
      }
    }
  }
  </script>

  <style scoped>
  .board { display: grid; gap: 6px; background: #0a6b2c; padding: 8px; border-radius: 8px; width: fit-content; }
  .row { display: grid; grid-template-columns: repeat(8, 48px); gap: 6px; }
  .cell { width: 48px; height: 48px; border-radius: 6px; border: none; background: #0f8c39; box-shadow: inset 0 0 0 2px #065f2a; cursor: pointer; font-size: 24px; line-height: 20px; color: #000; }
  .cell.hint { box-shadow: inset 0 0 0 3px #ffe08a; }
  .cell.black span { color: #111; }
  .cell.white span { color: #eee; text-shadow: 0 0 1px #333; }
  .cell:active { transform: translateY(1px); }
  </style>