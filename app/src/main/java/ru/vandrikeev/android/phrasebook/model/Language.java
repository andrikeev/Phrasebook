package ru.vandrikeev.android.phrasebook.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.vandrikeev.android.phrasebook.R;

/**
 * Available languages. Enum names should be written in UPPER_CASE, but API returns lowercase codes so it is
 * trading convention with convenience.
 * <p>
 * There is also a problem that when a new language appears in the API, errors may occur during deserialization, but
 * again, simplicity leaves no choice.
 */
@SuppressWarnings("unused")
public enum Language {

    auto(R.string.spinner_autodetect),
    az(R.string.az),
    sq(R.string.sq),
    am(R.string.am),
    en(R.string.en),
    ar(R.string.ar),
    hy(R.string.hy),
    af(R.string.af),
    eu(R.string.eu),
    ba(R.string.ba),
    be(R.string.be),
    bn(R.string.bn),
    bg(R.string.bg),
    bs(R.string.bs),
    cy(R.string.cy),
    hu(R.string.hu),
    vi(R.string.vi),
    ht(R.string.ht),
    gl(R.string.gl),
    nl(R.string.nl),
    mrj(R.string.mrj),
    el(R.string.el),
    ka(R.string.ka),
    gu(R.string.gu),
    da(R.string.da),
    he(R.string.he),
    yi(R.string.yi),
    id(R.string.id),
    ga(R.string.ga),
    it(R.string.it),
    is(R.string.is),
    es(R.string.es),
    kk(R.string.kk),
    kn(R.string.kn),
    ca(R.string.ca),
    ky(R.string.ky),
    zh(R.string.zh),
    ko(R.string.ko),
    xh(R.string.xh),
    la(R.string.la),
    lv(R.string.lv),
    lt(R.string.lt),
    lb(R.string.lb),
    mg(R.string.mg),
    ms(R.string.ms),
    ml(R.string.ml),
    mt(R.string.mt),
    mk(R.string.mk),
    mi(R.string.mi),
    mr(R.string.mr),
    mhr(R.string.mhr),
    mn(R.string.mn),
    de(R.string.de),
    ne(R.string.ne),
    no(R.string.no),
    pa(R.string.pa),
    pap(R.string.pap),
    fa(R.string.fa),
    pl(R.string.pl),
    pt(R.string.pt),
    ro(R.string.ro),
    ru(R.string.ru),
    ceb(R.string.ceb),
    sr(R.string.sr),
    si(R.string.si),
    sk(R.string.sk),
    sl(R.string.sl),
    sw(R.string.sw),
    su(R.string.su),
    tg(R.string.tg),
    th(R.string.th),
    tl(R.string.tl),
    ta(R.string.ta),
    tt(R.string.tt),
    te(R.string.te),
    tr(R.string.tr),
    udm(R.string.udm),
    uz(R.string.uz),
    uk(R.string.uk),
    ur(R.string.ur),
    fi(R.string.fi),
    fr(R.string.fr),
    hi(R.string.hi),
    hr(R.string.hr),
    cs(R.string.cs),
    sv(R.string.sv),
    gd(R.string.gd),
    et(R.string.et),
    eo(R.string.eo),
    jv(R.string.jv),
    ja(R.string.ja),
    //
    ;

    private static List<Language> values;

    private int nameResId;

    Language(int nameResId) {
        this.nameResId = nameResId;
    }

    /**
     * All enum language values include 'special' value - Autodetect.
     *
     * @return list of enum values
     */
    @NonNull
    public static List<Language> getValues() {
        if (values == null) {
            values = new ArrayList<>(Language.values().length);
            Collections.addAll(values, Language.values());
        }
        return values;
    }

    /**
     * All enum language values except 'special' value - Autodetect.
     *
     * @return list of enum values
     */
    @NonNull
    public static List<Language> getLanguageOnlyValues() {
        List<Language> languages = new ArrayList<>(getValues());
        int idx = languages.indexOf(auto);
        languages.remove(idx);
        return languages;
    }

    public int getNameResId() {
        return nameResId;
    }

    @NonNull
    public String getLocalizedName(@NonNull Context context) {
        return context.getString(this.nameResId);
    }
}
