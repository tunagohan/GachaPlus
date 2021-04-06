# GachaPlus

Fork By [Gacha Plugin](https://github.com/gorogoro-space/Gacha).

## Required

- Spigot 1.16.5+
- [Vault](https://www.spigotmc.org/resources/vault.34315/)

## Difference from　Gacha

The original Gacha issued unique gacha tickets and used to play gacha games.

Therefore, it was not possible to create duplicate items, and when duplicating, it was necessary to hit the command accordingly.

Also, because it is unique, it could not be sold with the ChestShop Plugin.

Therefore, it was impossible to change the price range etc. with high-end gachas and limited gachas.

What made it possible is Gacha Plus.

## How to use

### 1) Setting Sign

Set up a sign.

Please enter in the following order on the signboard.

The `[gacha]` on the first line is a description to enable it, so it is absolutely necessary.

```
[gacha]
<gacha name>
<gacha display name>
<price>
```

example

```
[gacha]
sample_gacha
季節のガチャ
2500
```

### 2) Modify Chest

Enter the following command and left-click on the "Chest" you want to enable.

`/gachaplus modify sample_gacha`

done.

# 日本語説明

## Gachaとの違い

オリジナルのGachaはユニークなガチャチケットを発行し、
ガチャをするような仕組みでした。

その為重複するアイテムを作成することができず、
複製する場合はそれに応じてコマンドを入力する必要がありました。

また、ユニークであるためChestShopプラグインで販売することはできませんでした。
それゆえに、高級ガチャや限定ガチャでは
価格帯等を変更することが出来なかったり差別化することができませんでした。

それを可能にしたのがガチャプラスです。


## 使い方


### 1) 看板の設定

看板を設置します。

看板には以下の順に入力してください。

1行目の`[gacha]`は有効にするための記述なので必ず必要です。

```
[gacha]
<gacha name>
<gacha display name>
<price>
```

例

```
[gacha]
sample_gacha
季節のガチャ
2500
```

### 2) チェストを有効にする

次のコマンドを入力し、有効にする「チェスト」を左クリックします。

`/gachaplus modify sample_gacha`

以上
