package yuyuko.relics

import basemod.abstracts.CustomRelic
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction
import com.megacrit.cardcrawl.cards.DamageInfo
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.ImageMaster
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.relics.AbstractRelic
import yuyuko.cards.yuyuko.Butterfly
import yuyuko.cards.yuyuko.Sakura
import yuyuko.powers.FanPower
import kotlin.math.min


class Yuyukosfan : CustomRelic(
        ID,
        ImageMaster.loadImage(IMAGE_PATH),
        RelicTier.STARTER,
        LandingSound.MAGICAL
) {
    companion object {
        @JvmStatic
        val ID = "Yuyuko's fan"
        val IMAGE_PATH = "images/relics/fan.png"
    }

    val fanAmount: Int
        get() = counter

    init {
        this.counter = 5
        updateDescription()
    }

    private var turns = 0
    var turnsOfLastBattle = 0
        private set

    private var lostBlock = 0

    override fun atTurnStart() {
        turns++
        lostBlock = AbstractDungeon.player.currentBlock
    }

    override fun atTurnStartPostDraw() {
        AbstractDungeon.player.currentBlock = lostBlock
    }

    override fun atBattleStartPreDraw() {
        turns = 0

        this.updateDescription()

        val player = AbstractDungeon.player

        val amount = if (player.hasRelic(TripToHell.ID)) {
            fanAmount * 2
        } else {
            fanAmount
        }
        AbstractDungeon.actionManager.addToBottom(
                ApplyPowerAction(
                        player, player,
                        FanPower(amount),
                        amount
                )
        )
        AbstractDungeon.actionManager.addToBottom(
                MakeTempCardInDrawPileAction(
                        Sakura(), fanAmount, true, true
                )
        )
        AbstractDungeon.actionManager.addToBottom(
                MakeTempCardInDrawPileAction(
                        Butterfly(), fanAmount, true, true
                )
        )
    }

    override fun onRest() {
        counter += 1
        updateDescription()
        flash()
    }

    override fun onVictory() {
        this.flash()
        val damage = min(turns, AbstractDungeon.player.currentHealth - 1)
        AbstractDungeon.player.damage(DamageInfo(null, damage, HP_LOSS))
        turnsOfLastBattle = damage
    }

    fun updateDescription() {
        this.description = updatedDescription
        this.tips.clear()
        this.tips.add(PowerTip(this.name, this.description))
        initializeTips()
    }

    override fun getUpdatedDescription(): String {
        return DESCRIPTIONS[0] + fanAmount + DESCRIPTIONS[1] + fanAmount + DESCRIPTIONS[2]
    }

    override fun makeCopy(): AbstractRelic {
        return Yuyukosfan()
    }


}