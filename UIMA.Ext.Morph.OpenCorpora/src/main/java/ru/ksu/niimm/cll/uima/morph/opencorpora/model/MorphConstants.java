/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphConstants {

	public static final String POST = "POST"; // часть речи
	public static final String NOUN = "NOUN"; // имя существительное
	public static final String ADJF = "ADJF"; // имя прилагательное (полное)
	public static final String ADJS = "ADJS"; // имя прилагательное (краткое)
	public static final String COMP = "COMP"; // компаратив
	public static final String VERB = "VERB"; // глагол (личная форма)
	public static final String INFN = "INFN"; // глагол (инфинитив)
	public static final String PRTF = "PRTF"; // причастие (полное)
	public static final String PRTS = "PRTS"; // причастие (краткое)
	public static final String GRND = "GRND"; // деепричастие
	public static final String NUMR = "NUMR"; // числительное
	public static final String ADVB = "ADVB"; // наречие
	public static final String NPRO = "NPRO"; // местоимение-существительное
	public static final String PRED = "PRED"; // предикатив
	public static final String PREP = "PREP"; // предлог
	public static final String CONJ = "CONJ"; // союз
	public static final String PRCL = "PRCL"; // частица
	public static final String INTJ = "INTJ"; // междометие
	public static final String ANim = "ANim"; // одушевлённость / одушевлённость не выражена
	public static final String anim = "anim"; // одушевлённое
	public static final String inan = "inan"; // неодушевлённое
	public static final String GNdr = "GNdr"; // род / род не выражен
	public static final String masc = "masc"; // мужской род
	public static final String femn = "femn"; // женский род
	public static final String neut = "neut"; // средний род
	public static final String comgend = "ор";//	общий род	—
	public static final String NMbr = "NMbr"; // число
	public static final String sing = "sing"; // единственное число
	public static final String plur = "plur"; // множественное число
	public static final String Sgtm = "Sgtm"; // singularia tantum
	public static final String Pltm = "Pltm"; // pluralia tantum
	public static final String Fixd = "Fixd"; // неизменяемое
	public static final String CAse = "CAse"; // категория падежа
	public static final String nomn = "nomn"; // именительный падеж
	public static final String gent = "gent"; // родительный падеж
	public static final String datv = "datv"; // дательный падеж
	public static final String accs = "accs"; // винительный падеж
	public static final String ablt = "ablt"; // творительный падеж
	public static final String loct = "loct"; // предложный падеж
	public static final String voct = "voct"; // звательный падеж
	public static final String gen1 = "gen1"; // первый родительный падеж
	public static final String gen2 = "gen2"; // второй родительный (частичный) падеж
	public static final String acc2 = "acc2"; // второй винительный падеж
	public static final String loc1 = "loc1"; // первый предложный падеж
	public static final String loc2 = "loc2"; // второй предложный (местный) падеж
	public static final String Abbr = "Abbr"; // аббревиатура
	public static final String Name = "Name"; // имя
	public static final String Surn = "Surn"; // фамилия
	public static final String Patr = "Patr"; // отчество
	public static final String Geox = "Geox"; // топоним
	public static final String Orgn = "Orgn"; // организация
	public static final String Trad = "Trad"; // торговая марка
	public static final String Subx = "Subx"; // возможна субстантивация
	public static final String Supr = "Supr"; // превосходная степень
	public static final String Qual = "Qual"; // качественное
	public static final String Apro = "Apro"; // местоименное
	public static final String Anum = "Anum"; // порядковое
	public static final String Poss = "Poss"; // притяжательное
	// 63	V-ey	*ею	форма на -ею	—
	// 64	V-oy	*ою	форма на -ою	—
	public static final String Cmp2 = "Cmp2"; // сравнительная степень на по-
	// 66	V-ej	*ей	форма компаратива на -ей	—
	public static final String ASpc = "ASpc"; // категория вида
	public static final String perf = "perf"; // совершенный вид
	public static final String impf = "impf"; // несовершенный вид
	public static final String TRns = "TRns"; // категория переходности
	public static final String tran = "tran"; // переходный
	public static final String intr = "intr"; // непереходный
	public static final String Impe = "Impe"; // безличный
	public static final String Uimp = "Uimp"; // безличное употребление
	public static final String Mult = "Mult"; // многократный
	public static final String Refl = "Refl"; // возвратный
	public static final String PErs = "PErs"; // категория лица
	public static final String per1 = "1per"; // 1 лицо
	public static final String per2 = "2per"; // 2 лицо
	public static final String per3 = "3per"; // 3 лицо
	public static final String TEns = "TEns"; // категория времени
	public static final String pres = "pres"; // настоящее время
	public static final String past = "past"; // прошедшее время
	public static final String futr = "futr"; // будущее время
	public static final String MOod = "MOod"; // категория наклонения
	public static final String indc = "indc"; // изъявительное наклонение
	public static final String impr = "impr"; // повелительное наклонение
	public static final String INvl = "INvl"; // категория совместности
	public static final String incl = "incl"; // говорящий включён в действие
	public static final String excl = "excl"; // говорящий не включён в действие
	public static final String VOic = "VOic"; // категория залога
	public static final String actv = "actv"; // действительный залог
	public static final String pssv = "pssv"; // страдательный залог

	private MorphConstants() {
	}
}