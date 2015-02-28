package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rinat Gareev
 */
interface GramTiers {
    int getCount();

    Set<String> getTierCategories(int i);

    BitSet getTierMask(int i);

    String getTierId(int i);
}

class GramTiersFactory {

    private static final Splitter posCatSplitter = Splitter.on('&').trimResults();
    // split tier definitions in a single line definition string
    public static final Splitter tierSplitter = Splitter.on('|').trimResults();

    static GramTiers parseGramTiers(final GramModel gramModel, String defString) {
        List<String> tierDefs = Lists.newArrayList(tierSplitter.split(defString));
        return parseGramTiers(gramModel, tierDefs);
    }

    static GramTiers parseGramTiers(final GramModel gramModel, List<String> paramValList) {
        List<Set<String>> tierCatsList = Lists.newArrayList();
        List<String> tierIds = Lists.newArrayList();
        for (String tierDef : paramValList) {
            Set<String> tierCats = ImmutableSet.copyOf(posCatSplitter.split(tierDef));
            tierIds.add(tierDef);
            if (tierCats.isEmpty()) {
                throw new IllegalStateException(String.format("Illegal posTiers parameter value"));
            }
            tierCatsList.add(tierCats);
        }
        final List<String> finalTierIds = ImmutableList.copyOf(tierIds);
        final List<Set<String>> finalTierCatsList = ImmutableList.copyOf(tierCatsList);
        final List<BitSet> tierMasks = ImmutableList.copyOf(
                Lists.transform(finalTierCatsList, new Function<Set<String>, BitSet>() {
                    @Override
                    public BitSet apply(Set<String> input) {
                        return makeBitMask(gramModel, input);
                    }
                }));
        return new GramTiers() {
            @Override
            public int getCount() {
                return finalTierCatsList.size();
            }

            @Override
            public Set<String> getTierCategories(int i) {
                return finalTierCatsList.get(i);
            }

            @Override
            public BitSet getTierMask(int i) {
                return tierMasks.get(i);
            }

            @Override
            public String getTierId(int i) {
                return finalTierIds.get(i);
            }
        };
    }

    /**
     * @param gramCats
     * @return bit mask for all PoS-categories in argument gramCats
     */
    private static BitSet makeBitMask(GramModel gramModel, Iterable<String> gramCats) {
        BitSet result = new BitSet();
        for (String posCat : gramCats) {
            BitSet posCatBits = gramModel.getGrammemWithChildrenBits(posCat, true);
            if (posCatBits == null) {
                throw new IllegalStateException(String.format(
                        "Unknown grammeme (category): %s", posCat));
            }
            result.or(posCatBits);
        }
        return result;
    }

    private GramTiersFactory() {
    }
}