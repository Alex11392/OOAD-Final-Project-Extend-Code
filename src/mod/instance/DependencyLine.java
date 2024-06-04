package mod.instance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;
import java.lang.Math;

public class DependencyLine extends JPanel
		implements IFuncComponent, ILinePainter
{
	JPanel				from;
	int					fromSide;
	Point				fp				= new Point(0, 0);
	JPanel				to;
	int					toSide;
	Point				tp				= new Point(0, 0);
	boolean				isSelect		= false;
	int					selectBoxSize	= 5;
	CanvasPanelHandler	cph;

	public DependencyLine(CanvasPanelHandler cph)
	{
		this.setOpaque(false);
		this.setVisible(true);
		this.setMinimumSize(new Dimension(1, 1));
		this.cph = cph;
	}

	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // 使用 Graphics2D 來繪製虛線
        Graphics2D g2d = (Graphics2D) g;
    
        // 設置虛線的樣式
        float[] dashPattern = {5, 5}; // 虛線的長度和間距
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
    
        // 計算相對於面板位置的連接點
        renewConnect();
        Point fpPrime = new Point(fp.x - this.getLocation().x, fp.y - this.getLocation().y);
        Point tpPrime = new Point(tp.x - this.getLocation().x, tp.y - this.getLocation().y);
    
        // 繪製虛線
        g2d.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
    
        // 繪製箭頭
        int x1 = fpPrime.x;
        int y1 = fpPrime.y;
        int x2 = tpPrime.x;
        int y2 = tpPrime.y; 
        // 计算箭头的角度和长度
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 14; // 箭头大小可以根据需要调整
        // 计算箭头的三个点的坐标
        int[] xPoints = new int[2];
        int[] yPoints = new int[2];
        xPoints[0] = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        yPoints[0] = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        xPoints[1] = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        yPoints[1] = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

		// 临时设置为实线
		g2d.setStroke(new BasicStroke(1));
		
        g2d.drawLine(xPoints[0], yPoints[0], x2, y2);
        g2d.drawLine(xPoints[1], yPoints[1], x2, y2);
    
        // 如果選中，繪製選中效果
        if (isSelect == true) {
            paintSelect(g2d);
        }
    }


	@Override
	public void reSize()
	{
		Dimension size = new Dimension(Math.abs(fp.x - tp.x) + 10,
				Math.abs(fp.y - tp.y) + 10);
		this.setSize(size);
		this.setLocation(Math.min(fp.x, tp.x) - 5, Math.min(fp.y, tp.y) - 5);
	}

	@Override
	public void paintArrow(Graphics g, Point point)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setConnect(DragPack dPack)
	{
		Point mfp = dPack.getFrom();
		Point mtp = dPack.getTo();
		from = (JPanel) dPack.getFromObj();
		to = (JPanel) dPack.getToObj();
		fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(),
				mfp);
		toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);
		renewConnect();
		System.out.println("from side " + fromSide);
		System.out.println("to side " + toSide);
	}

	void renewConnect()
	{
		try
		{
			fp = getConnectPoint(from, fromSide);
			tp = getConnectPoint(to, toSide);
			this.reSize();
		}
		catch (NullPointerException e)
		{
			this.setVisible(false);
			cph.removeComponent(this);
		}
	}

	Point getConnectPoint(JPanel jp, int side)
	{
		Point temp = new Point(0, 0);
		Point jpLocation = cph.getAbsLocation(jp);
		if (side == new AreaDefine().TOP)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = jpLocation.y;
		}
		else if (side == new AreaDefine().RIGHT)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		}
		else if (side == new AreaDefine().LEFT)
		{
			temp.x = jpLocation.x;
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		}
		else if (side == new AreaDefine().BOTTOM)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
		}
		else
		{
			temp = null;
			System.err.println("getConnectPoint fail:" + side);
		}
		return temp;
	}

	@Override
	public void paintSelect(Graphics gra)
	{
		gra.setColor(Color.BLACK);
		gra.fillRect(fp.x, fp.y, selectBoxSize, selectBoxSize);
		gra.fillRect(tp.x, tp.y, selectBoxSize, selectBoxSize);
	}

	public boolean isSelect()
	{
		return isSelect;
	}

	public void setSelect(boolean isSelect)
	{
		this.isSelect = isSelect;
	}
	
	//判斷Line是否在這個Port上
	public boolean isOnPort(Point p){
		if(p.equals(fp) || p.equals(tp)){
			return true;
		}
		else{
			return false;
		}
	}
}
