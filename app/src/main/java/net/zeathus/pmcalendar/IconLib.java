package net.zeathus.pmcalendar;

public class IconLib {

    public static int getItemIcon(String item) {

        switch (item) {
            case "Unknown": {
                return R.drawable.icon_unknown;
            }
            case "Coin":
            case "Coins": {
                return R.drawable.icon_coin;
            }
            case "Gem":
            case "Gems": {
                return R.drawable.icon_gem;
            }
            case "Aid Ade": {
                return R.drawable.icon_aid_ade;
            }
            case "Buff Blend": {
                return R.drawable.icon_buff_blend;
            }
            case "Tech Tonic": {
                return R.drawable.icon_tech_tonic;
            }
            case "Gym Leader Notes": {
                return R.drawable.icon_gym_leaders_notes;
            }
            case "Elite Four Notes": {
                return R.drawable.icon_elite_four_notes;
            }
            case "Champion Notes": {
                return R.drawable.icon_champion_notes;
            }
            case "3 Star Power-Up": {
                return R.drawable.icon_power_up_3;
            }
            case "4 Star Power-Up": {
                return R.drawable.icon_power_up_4;
            }
            case "5 Star Power-Up": {
                return R.drawable.icon_power_up_5;
            }
            case "Replay Ticket": {
                return R.drawable.icon_replay_ticket;
            }
            case "Skill Capsule": {
                return R.drawable.icon_skill_capsule;
            }
            case "Training Machine": {
                return R.drawable.icon_training_machine;
            }
            case "Pearl": {
                return R.drawable.icon_pearl;
            }
            case "Level-Up Manual": {
                return R.drawable.icon_level_manual;
            }
            case "Voucher A": {
                return R.drawable.icon_voucher_a;
            }
            case "Voucher B": {
                return R.drawable.icon_voucher_b;
            }
            case "Voucher C": {
                return R.drawable.icon_voucher_c;
            }
            case "Voucher D": {
                return R.drawable.icon_voucher_d;
            }
            case "Sync Pair": {
                return R.drawable.icon_sync_pair;
            }
        }

        return R.drawable.icon_default;
    }

}
