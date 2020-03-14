package me.syari.ss.core.command.create

enum class ErrorMessage(val message: String) {
    OnlyPlayer("コンソールから実行できないコマンドです"),
    NotEnterPlayer("プレイヤーを入力してください"),
    NotFoundPlayer("プレイヤーが見つかりませんでした"),
    NotEnterName("名前を入力してください"),
    AlreadyExist("既に存在しています")
}