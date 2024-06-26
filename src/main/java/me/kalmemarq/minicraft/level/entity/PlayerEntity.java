/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.level.entity;

import me.kalmemarq.minicraft.bso.BsoArrayTag;
import me.kalmemarq.minicraft.bso.BsoListTag;
import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.item.Inventory;
import me.kalmemarq.minicraft.level.item.ItemStack;
import me.kalmemarq.minicraft.level.item.Items;

import java.util.List;

public class PlayerEntity extends MobEntity {
    public int maxStamina = 10;
    public int stamina = this.maxStamina;
    public int staminaRecharge;
    public int staminaRechargeDelay;
    public ItemStack attackItem;
    public ItemStack activeItem;
    protected int attackTime, attackDir;
    public Inventory inventory = new Inventory();
    public int score;

    public PlayerEntity(Level level) {
        super(level);
        this.x = 32 * 16;
        this.y = 32 * 16;

        if (!level.isClient()) {
            this.inventory.add(new ItemStack(Items.POWER_GLOVE));
        }
    }

	@Override
	public void write(BsoMapTag map) {
		super.write(map);
		map.put("score", this.score);
		map.put("maxStamina", this.maxStamina);
		map.put("stamina", this.stamina);
		map.put("staminaRecharge", this.staminaRecharge);
		map.put("staminaRechargeDelay", this.staminaRechargeDelay);
		if (this.attackItem != null) map.put("attackItem", this.attackItem.write(new BsoMapTag()));
		if (this.activeItem != null) map.put("activeItem", this.activeItem.write(new BsoMapTag()));
		map.put("attackTime", this.attackTime);
		map.put("attackDir", this.attackDir);

		BsoArrayTag m = new BsoArrayTag();
		for (ItemStack stack : this.inventory.itemStacks) {
			m.add(stack.write(new BsoMapTag()));
		}

		map.put("inventory", m);
	}

	@Override
    public boolean canSwim() {
        return true;
    }

    protected boolean use() {
        int yo = -2;
        if (this.dir == 0 && this.use(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + 12 + yo)) return true;
        if (this.dir == 1 && this.use(this.x - 8, this.y - 12 + yo, this.x + 8, this.y - 4 + yo)) return true;
        if (this.dir == 3 && this.use(this.x + 4, this.y - 8 + yo, this.x + 12, this.y + 8 + yo)) return true;
        if (this.dir == 2 && this.use(this.x - 12, this.y - 8 + yo, this.x - 4, this.y + 8 + yo)) return true;

        int xt = this.x >> 4;
        int yt = (this.y + yo) >> 4;
        int r = 12;
        if (this.attackDir == 0) yt = (this.y + r + yo) >> 4;
        if (this.attackDir == 1) yt = (this.y - r + yo) >> 4;
        if (this.attackDir == 2) xt = (this.x - r) >> 4;
        if (this.attackDir == 3) xt = (this.x + r) >> 4;

        if (xt >= 0 && yt >= 0 && xt < this.level.width && yt < this.level.height) {
            return this.level.getTile(xt, yt).use(this.level, xt, yt, this, this.attackDir);
        }

        return false;
    }

    protected boolean use(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) if (e.use(this, this.attackDir)) return true;
        }
        return false;
    }

    protected void attack() {
        this.walkDist += 8;
        this.attackDir = this.dir;
        this.attackItem = this.activeItem;
        boolean done = false;

        if (this.activeItem != null) {
            this.attackTime = 10;
            int yo = -2;
            int range = 12;
            if (this.dir == 0 && this.interact(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + range + yo)) done = true;
            if (this.dir == 1 && this.interact(this.x - 8, this.y - range + yo, this.x + 8, this.y - 4 + yo)) done = true;
            if (this.dir == 3 && this.interact(this.x + 4, this.y - 8 + yo, this.x + range, this.y + 8 + yo)) done = true;
            if (this.dir == 2 && this.interact(this.x - range, this.y - 8 + yo, this.x - 4, this.y + 8 + yo)) done = true;
            if (done) return;

            int xt = this.x >> 4;
            int yt = (this.y + yo) >> 4;
            int r = 12;
            if (this.attackDir == 0) yt = (this.y + r + yo) >> 4;
            if (this.attackDir == 1) yt = (this.y - r + yo) >> 4;
            if (this.attackDir == 2) xt = (this.x - r) >> 4;
            if (this.attackDir == 3) xt = (this.x + r) >> 4;

            if (xt >= 0 && yt >= 0 && xt < this.level.width && yt < this.level.height) {
                if (this.activeItem.interactOn(this.level.getTile(xt, yt), this.level, xt, yt, this, this.attackDir)) {
                    done = true;
                } else {
                    if (this.level.getTile(xt, yt).interact(this.level, xt, yt, this, this.activeItem, this.attackDir)) {
                        done = true;
                    }
                }
                if (this.activeItem.isDepleted()) {
                    this.activeItem = null;
                }
            }
        }

        if (done) return;

        if (this.activeItem == null || this.activeItem.canAttack()) {
            this.attackTime = 5;
            int yo = -2;
            int range = 20;
            if (this.dir == 0) this.hurt(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + range + yo);
            if (this.dir == 1) this.hurt(this.x - 8, this.y - range + yo, this.x + 8, this.y - 4 + yo);
            if (this.dir == 3) this.hurt(this.x + 4, this.y - 8 + yo, this.x + range, this.y + 8 + yo);
            if (this.dir == 2) this.hurt(this.x - range, this.y - 8 + yo, this.x - 4, this.y + 8 + yo);

            int xt = this.x >> 4;
            int yt = (this.y + yo) >> 4;
            int r = 12;
            if (this.attackDir == 0) yt = (this.y + r + yo) >> 4;
            if (this.attackDir == 1) yt = (this.y - r + yo) >> 4;
            if (this.attackDir == 3) xt = (this.x - r) >> 4;
            if (this.attackDir == 2) xt = (this.x + r) >> 4;

            if (xt >= 0 && yt >= 0 && xt < this.level.width && yt < this.level.height) {
                this.level.getTile(xt, yt).hurt(this.level, xt, yt, this, this.random.nextInt(3) + 1, this.attackDir);
            }
        }
    }

    private boolean interact(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) if (e.interact(this, this.activeItem, this.attackDir)) return true;
        }
        return false;
    }

    private void hurt(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) e.hurt(this, this.getAttackDamage(e), this.attackDir);
        }
    }

    private int getAttackDamage(Entity e) {
        int dmg = this.random.nextInt(3) + 1;
        if (this.attackItem != null) {
            dmg += this.attackItem.getAttackDamageBonus(e);
        }
        return dmg;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.stamina <= 0 && this.staminaRechargeDelay == 0 && this.staminaRecharge == 0) {
            this.staminaRechargeDelay = 40;
        }

        if (this.staminaRechargeDelay > 0) {
            this.staminaRechargeDelay--;
        }

        if (this.staminaRechargeDelay == 0) {
            this.staminaRecharge++;
            if (this.isSwimming()) {
                this.staminaRecharge = 0;
            }
            while (this.staminaRecharge > 10) {
                this.staminaRecharge -= 10;
                if (this.stamina < this.maxStamina && !this.level.isClient()) this.stamina++;
            }
        }

        if (this.isSwimming() && this.tickTime % 60 == 0) {
            if (this.stamina > 0) {
                if (!this.level.isClient()) this.stamina--;
            } else {
                if (!this.level.isClient()) this.hurt(this, 1, this.dir ^ 1);
            }
        }
    }

    public boolean payStamina(int staminaCost) {
        return false;
    }

    @Override
    public boolean findStartPos(Level level) {
        while (true) {
            int x = this.random.nextInt(level.width);
            int y = this.random.nextInt(level.height);
            int xx = x * 16 + 8;
            int yy = y * 16 + 8;

            if (level.getTile(x, y).mayPass(level, x, y, this) && level.getTile(x, y).getNumericId() == 0) {
                this.x = xx;
                this.y = yy;
                return true;
            }
        }
    }

    public void touchItem(ItemEntity itemEntity) {
        if (!this.level.isClient()) {
            itemEntity.take(this);
            this.inventory.add(itemEntity.stack);
        }
    }

    @Override
    protected void touchedBy(Entity entity) {
        if (!(entity instanceof PlayerEntity)) {
            entity.touchedBy(this);
        }
    }
}
