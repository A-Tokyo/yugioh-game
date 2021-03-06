package eg.edu.guc.yugioh.gui.boardframe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import eg.edu.guc.yugioh.cards.Card;
import eg.edu.guc.yugioh.cards.MonsterCard;
import eg.edu.guc.yugioh.cards.spells.ChangeOfHeart;
import eg.edu.guc.yugioh.cards.spells.MagePower;
import eg.edu.guc.yugioh.cards.spells.SpellCard;
import eg.edu.guc.yugioh.gui.GUI;

@SuppressWarnings("serial")
public class HandOptionsFrame extends JFrame implements ActionListener{

	JButton leftButton = new JButton ("Summon Monster");
	JButton rightButton = new JButton ("Set Monster");
	JButton cancelButton = new JButton ("Cancel");
	SpellCard spell;
	MonsterCard monster;
	public HandOptionsFrame(boolean isMonsterOptions , Card card){
		super("Choose Action");
		if(!isMonsterOptions){
			spell = (SpellCard)card;
			leftButton.setText("Activate Spell");
			rightButton.setText("Set Spell");
		}
		else
			monster = (MonsterCard)card;
		constructFrame();
	}

	private void constructFrame() {
		setVisible(true);
		setLayout(new GridBagLayout());
		setSize(300,150);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.insets.top = 10;
		c.insets.right = 5;
		c.gridy = 0;
		add(leftButton , c);
		c.gridx =1;
		c.insets.right = 0;
		c.ipadx = 25;
		add(rightButton , c);
		c.gridx = 0;
		c.ipadx = 0;
		c.gridy =1 ;
		c.gridwidth =2;
		add(cancelButton , c);
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Cancel")){
			GUI.getBoardFrame().resetHandlers();
		}else
			if(e.getActionCommand().equals("Activate Spell")){
				activateSpellCard();
			}
			else
				if(e.getActionCommand().equals("Set Spell")){
					try {
						Card.getBoard().getActivePlayer().setSpell(spell);
						GUI.getBoardFrame().setMonsterToSummon(null);
						GUI.getBoardFrame().setSpellToActivate(null);
					} catch (Exception e1) {
						GUI.errorFrame(e1);
					}
				}else
					monsterOptions(e);
		GUI.getBoardFrame().updateBoardFrame();
		dispose();
	}

	private void activateSpellCard() {
		if((spell instanceof ChangeOfHeart || spell instanceof MagePower)){
			new ConfirmFrame("Please click a monster to activate on");
			GUI.getBoardFrame().setSpellToActivate(spell);
			GUI.getBoardFrame().setMonsterToSummon(null);
		}else{
			try {
				Card.getBoard().getActivePlayer().activateSpell(spell, null);
				GUI.getBoardFrame().setMonsterToSummon(null);
				GUI.getBoardFrame().setSpellToActivate(null);
			} catch (Exception e) {
				GUI.errorFrame(e);
			}
		}
	}
	private void monsterOptions(ActionEvent e){
		try{
			if(e.getActionCommand().equals("Summon Monster")){
				if(monster.getLevel()<5){
					Card.getBoard().getActivePlayer().summonMonster(monster);
					GUI.getBoardFrame().setMonsterToSummon(null);
				}
				else {
					ritualSummon(true);
				}
				GUI.getBoardFrame().setSpellToActivate(null);
			}
			if(e.getActionCommand().equals("Set Monster")){
				if(monster.getLevel()<5){
					Card.getBoard().getActivePlayer().setMonster(monster);
					GUI.getBoardFrame().setMonsterToSummon(null);
				}else {
					ritualSummon(false);
				}
				GUI.getBoardFrame().setSpellToActivate(null);
			}
			
		}catch(Exception e1){
			GUI.errorFrame(e1);
		}
	}
	private void ritualSummon(boolean isAttackMode) {
		if(monster.getLevel()<7)
			GUI.getBoardFrame().setSacrificesCount(1);
		else 
			GUI.getBoardFrame().setSacrificesCount(2);
		if(Card.getBoard().getActivePlayer().getField().getMonstersArea().size()>=GUI.getBoardFrame().getSacrificesCount()){
			new ConfirmFrame("Please click "+GUI.getBoardFrame().getSacrificesCount()+" monster(s) to sacrifice");
			GUI.getBoardFrame().setMonsterToSummon(monster);
			GUI.getBoardFrame().setSacrificeAttack(isAttackMode);
		}else{
			GUI.errorFrame(new Exception("You don't have enough sacrifices."));
		}
	}
}